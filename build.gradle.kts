import me.modmuss50.mpp.ReleaseType

plugins {
    id("me.modmuss50.mod-publish-plugin") version("1.1.0")
}

gradle.projectsEvaluated {
    publishMods {
        if (!project.hasProperty("build.release")) {
            return@publishMods println("Publishing is disabled; use the CI build workflow for test artifacts")
        }

        val modVersion = BuildConfig.createVersionString(project)
        type = when {
            modVersion.contains("alpha") -> ReleaseType.ALPHA
            modVersion.contains("beta") -> ReleaseType.BETA
            else -> ReleaseType.STABLE
        }
        changelog = BuildConfig.getChangelog(project)

        github {
            accessToken = project.providers.environmentVariable("GITHUB_TOKEN")
            repository = "Alutixx/Altium"
            commitish = BuildConfig.calculateGitHash(project)
            version = BuildConfig.RELEASE_TAG
            displayName = "Altium ${BuildConfig.MOD_VERSION} for Minecraft ${BuildConfig.MINECRAFT_VERSION}"
            file.unset()
            file.unsetConvention()
            allowEmptyFiles = true
        }
    }
}
