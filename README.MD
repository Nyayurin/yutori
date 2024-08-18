# Yutori

Kotlin Multiplatform Library

### Publish to MavenCentral

1) Registering a Sonatype account as described here: 
   https://dev.to/kotlin/how-to-build-and-publish-a-kotlin-multiplatform-library-going-public-4a8k
2) Add developer id, name, email and the project url to
   `/convention-plugins/src/main/kotlin/convention.publication.gradle.kts`
3) Add the secrets to `local.properties`:

```
signing.keyId=...
signing.password=...
signing.secretKeyRingFile=...
ossrhUsername=...
ossrhPassword=...
```

4) Run `./gradlew :yutori:publishAllPublicationsToSonatypeRepository`

### Build platform artifacts

#### Android aar

- Run `./gradlew :yutori:assembleRelease`
- Output: `/yutori/build/outputs/aar/yutori-release.aar`

#### JVM jar

- Run `./gradlew :yutori:jvmJar`
- Output: `/yutori/build/libs/yutori-jvm-1.0.jar`

#### iOS Framework

- Run `./gradlew :yutori:linkReleaseFrameworkIosArm64`
- Output: `/yutori/build/bin/iosArm64/releaseFramework/yutori.framework`

#### JS file

- Run `./gradlew :yutori:jsBrowserProductionWebpack`
- Output: `/yutori/build/dist/js/productionExecutable/yutori.js`

#### macOS Framework

- Run `./gradlew :yutori:linkReleaseFrameworkMacosArm64`
- Output: `/yutori/build/bin/macosArm64/releaseFramework/yutori.framework`

#### Linux static library

- Run `./gradlew :yutori:linkReleaseStaticLinuxX64`
- Output: `/yutori/build/bin/linuxX64/releaseStatic/libyutori.a`

#### Windows static library

- Run `./gradlew :yutori:linkReleaseStaticMingwX64`
- Output: `/yutori/build/bin/mingwX64/releaseStatic/libyutori.a`

#### Wasm binary file

- Run `./gradlew :yutori:wasmJsBrowserDistribution`
- Output: `/yutori/build/dist/wasmJs/productionExecutable/yutori-wasm-js.wasm`
