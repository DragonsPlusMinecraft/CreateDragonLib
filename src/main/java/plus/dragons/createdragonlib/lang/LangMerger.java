package plus.dragons.createdragonlib.lang;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

/*
MIT License

Copyright (c) 2019 simibubi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * A useful {@link DataProvider} which can merge handwritten localization files and generated ones into one. <br>
 * Originated from {@link com.simibubi.create.foundation.data.LangMerger} <br>
 */
@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
class LangMerger implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.create();
	private static final String CATEGORY_HEADER = "\t\"_\": \"->------------------------]  %s  [------------------------<-\",";
	private final String modid;
	private final String name;
	
	private final List<Object> mergedLangData = new ArrayList<>();
	private final Map<String, List<Object>> populatedLangData = new HashMap<>();
	private final Map<String, Map<String, String>> allLocalizedEntries = new HashMap<>();
	private final Map<String, MutableInt> missingTranslationTally = new HashMap<>();
	private final Map<String, MutableInt> existingButActuallyMissingTranslationTally = new HashMap<>();
	
	final List<LangPartial> partials = new ArrayList<>();
	DataGenerator dataGenerator;
	List<String> ignore = new ArrayList<>();

	LangMerger(String name, String modid) {
		this.modid = modid;
		this.name = name;
	}

	@Override
	public String getName() {
		return name + " Lang Merger";
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Path path = this.dataGenerator.getPackOutput().getOutputFolder()
			.resolve("assets/" + modid + "/lang/" + "en_us.json");

		return CompletableFuture.runAsync(() -> {
			for(Pair<String, JsonElement> pair : getAllLocalizationFiles()) {
				String langType = pair.getKey();
				if(!pair.getRight().isJsonObject())
					continue;
				Map<String, String> localizedEntries = new HashMap<>();
				JsonObject jsonObject = pair.getRight().getAsJsonObject();
				existingButActuallyMissingTranslationTally.put(langType, new MutableInt(0));
				jsonObject.entrySet()
						.stream()
						.forEachOrdered(entry -> {
							String key = entry.getKey();
							if(key.startsWith("_"))
								return;
							String value = entry.getValue()
									.getAsString();
							if(value.startsWith("UNLOCALIZED: "))
								existingButActuallyMissingTranslationTally.get(langType).increment();
							localizedEntries.put(key, value);
						});
				allLocalizedEntries.put(langType, localizedEntries);
				populatedLangData.put(langType, new ArrayList<>());
				missingTranslationTally.put(langType, new MutableInt(0));
			}

			try{
				collectExistingEntries(path);
				collectEntries();
				if(mergedLangData.isEmpty())
					return;

				save(cache, mergedLangData, -1, path, "Merging en_us.json with hand-written lang entries...");
				for(Entry<String, List<Object>> localization : populatedLangData.entrySet()) {
					String key = localization.getKey();
					Path populatedLangPath = this.dataGenerator.getPackOutput().getOutputFolder()
							.resolve("assets/" + modid + "/lang/unfinished/" + key);
					save(cache, localization.getValue(),
							missingTranslationTally.get(key).intValue() + existingButActuallyMissingTranslationTally.get(key).intValue(),
							populatedLangPath, "Populating " + key + " with missing entries...");
				}
			} catch (IOException e){
				LOGGER.error(e);
			}

		});
	}
	
	private boolean shouldIgnore(String key) {
		for (String string : ignore)
			if (key.startsWith(string))
				return true;
		return false;
	}

	private void collectExistingEntries(Path path) throws IOException {
		if (!Files.exists(path)) {
			LOGGER.warn("Nothing to merge! It appears no lang was generated before me.");
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);

			/*
			 * Erase additional sections from previous lang in case registrate did not
			 * create a new one (this assumes advancements to be the first section after
			 * game elements)
			 */
			Set<String> keysToRemove = new HashSet<>();
			MutableBoolean startErasing = new MutableBoolean();
			jsonobject.entrySet()
					.stream()
					.forEachOrdered(entry -> {
						String key = entry.getKey();
						if (key.startsWith("advancement"))
							startErasing.setTrue();
						if (startErasing.isFalse())
							return;
						keysToRemove.add(key);
					});
			jsonobject.remove("_");
			keysToRemove.forEach(jsonobject::remove);

			addAll("Game Elements", jsonobject);
		}
	}

	private void addAll(String header, JsonObject jsonObject) {
		header = String.format(CATEGORY_HEADER, header);

		writeData("\n");
		writeData(header);
		writeData("\n\n");

		MutableObject<String> previousKey = new MutableObject<>("");
		jsonObject.entrySet()
			.stream()
			.forEachOrdered(entry -> {
				String key = entry.getKey();
				if (shouldIgnore(key))
					return;
				String value = entry.getValue()
					.getAsString();
				if (!previousKey.getValue()
					.isEmpty() && shouldAddLineBreak(key, previousKey.getValue()))
					writeData("\n");
				writeEntry(key, value);
				previousKey.setValue(key);
			});

		writeData("\n");
	}

	private void writeData(String data) {
		mergedLangData.add(data);
		populatedLangData.values()
			.forEach(l -> l.add(data));
	}

	private void writeEntry(String key, String value) {
		mergedLangData.add(new LangEntry(key, value));
		populatedLangData.forEach((k, l) -> {
			ForeignLangEntry entry = new ForeignLangEntry(key, value, allLocalizedEntries.get(k));
			if (entry.isMissing())
				missingTranslationTally.get(k)
					.increment();
			l.add(entry);
		});
	}

	protected boolean shouldAddLineBreak(String key, String previousKey) {
		// Always put tooltips and ponder scenes in their own paragraphs
		if (key.endsWith(".tooltip"))
			return true;
		if (key.startsWith(modid + ".ponder") && key.endsWith(PonderScene.TITLE_KEY))
			return true;

		key = key.replaceFirst("\\.", "");
		previousKey = previousKey.replaceFirst("\\.", "");

		String[] split = key.split("\\.");
		String[] split2 = previousKey.split("\\.");
		if (split.length == 0 || split2.length == 0)
			return false;

		// Start new paragraph if keys before second point do not match
		return !split[0].equals(split2[0]);
	}

	private List<Pair<String, JsonElement>> getAllLocalizationFiles() {
		ArrayList<Pair<String, JsonElement>> list = new ArrayList<>();

		String filepath = "assets/" + modid + "/lang/";
		try (InputStream resourceStream = ClassLoader.getSystemResourceAsStream(filepath)) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceStream));
			while (true) {
				String readLine = bufferedReader.readLine();
				if (readLine == null)
					break;
				if (!readLine.endsWith(".json"))
					continue;
				if (readLine.startsWith("en_us") || readLine.startsWith("en_ud"))
					continue;
				list.add(Pair.of(readLine, FilesHelper.loadJsonResource(filepath + readLine)));
			}
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}

		return list;
	}

	private void collectEntries() {
		for (LangPartial partial : partials){
			addAll(partial.getDisplay(), partial.provide().getAsJsonObject());
		}
	}

	@SuppressWarnings("deprecation")
	private void save(CachedOutput cache, List<Object> dataIn, int missingKeys, Path target, String message)
			throws IOException {
		LOGGER.info(message);

		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

		Writer writer = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
		writer.append(createString(dataIn, missingKeys));
		writer.close();

		cache.writeIfNeeded(target, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
	}

	private String createString(List<Object> data, int missingKeys) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n");
		if (missingKeys != -1)
			builder.append("\t\"_\": \"Missing Localizations: ").append(missingKeys).append("\",\n");
		data.forEach(builder::append);
		builder.append("\t\"_\": \"Thank you for translating ").append(name).append("!\"\n\n");
		builder.append("}");
		return builder.toString();
	}

	private static class LangEntry {
		static final String ENTRY_FORMAT = "\t\"%s\": %s,\n";

		private final String key;
		private final String value;

		LangEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return String.format(ENTRY_FORMAT, key, GSON.toJson(value, String.class));
		}

	}

	private static class ForeignLangEntry extends LangEntry {

		private final boolean missing;

		ForeignLangEntry(String key, String value, Map<String, String> localizationMap) {
			super(key, localizationMap.getOrDefault(key, "UNLOCALIZED: " + value));
			missing = !localizationMap.containsKey(key);
		}

		public boolean isMissing() {
			return missing;
		}

	}

}
