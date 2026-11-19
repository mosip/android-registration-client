# AGENTS.md — android/transliterationmanager/

Parent guide: [`../AGENTS.md`](../AGENTS.md)

## Purpose

The smallest of the 4 manager modules — a thin wrapper around ICU4J's
`Transliterator` for script conversion between languages during
registration data entry (e.g. transliterating a name typed in one
script into another). No custom transliteration rules/tables of its
own beyond what ICU4J ships.

## Layout

```text
transliterationmanager/
├── build.gradle
└── src/
    ├── main/java/io/mosip/registration/transliterationmanager/
    │   ├── spi/TransliterationService.java
    │   └── service/TransliterationServiceImpl.java
    ├── test/java/.../TransliterationServiceUnitTest.java
    └── androidTest/java/.../ExampleInstrumentedTest.java   # default Android Studio stub, not a real test
```

No module-level README. This is effectively the entire module (full
source, for reference):

```java
public interface TransliterationService {
    String transliterate(String inputCode, String outputCode, String input);
}

public class TransliterationServiceImpl implements TransliterationService {
    @Override
    public String transliterate(String inputCode, String outputCode, String input) {
        if (inputCode.equals(outputCode)) return input;
        Transliterator transliterator = Transliterator.getInstance(inputCode + "-" + outputCode);
        return transliterator.transliterate(input);
    }
}
```

`inputCode`/`outputCode` are ICU4J transliterator IDs (e.g.
`"Devanagari-Latin"`), combined as `"<in>-<out>"` and passed straight to
`Transliterator.getInstance(...)`.

**No Pigeon references in this module.** The Pigeon `HostApi`
implementation (`TransliterationApi.java`) lives in `android/app`'s
`api_services/` and delegates to `TransliterationService` — see
`../AGENTS.md`.

## Build & Test Commands

```bash
cd android
./gradlew :transliterationmanager:assembleDebug
./gradlew :transliterationmanager:testDebugUnitTest
```

## Configuration

Single non-test dependency: `com.ibm.icu:icu4j` (version from the root
`ext` block — see `../AGENTS.md`). No `project(...)` module dependencies.

## Agent rules

### Do

1. Keep this module a thin pass-through to ICU4J — if you need custom
   transliteration behavior ICU4J doesn't provide, discuss the approach
   before adding a second transliteration mechanism here.

### Do not

1. Do not add Pigeon imports/interfaces to this module.
2. Do not treat `src/androidTest/.../ExampleInstrumentedTest.java` as a
   real test — it's the unmodified Android Studio project template stub.
