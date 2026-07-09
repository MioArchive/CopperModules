val jitpackGroup = System.getenv("GROUP")
val jitpackArtifact = System.getenv("ARTIFACT")

group = if (System.getenv("JITPACK") == "true" && !jitpackGroup.isNullOrBlank() && !jitpackArtifact.isNullOrBlank()) {
    "$jitpackGroup.$jitpackArtifact"
} else {
    "net.javamio.coppermodule"
}
description = ""
version = "1.0.0"
