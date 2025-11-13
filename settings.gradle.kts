pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Add the repository for the Google AI SDK
        maven { url = uri("https://storage.googleapis.com/generative-ai-android/maven/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Add the repository for the Google AI SDK first
        maven { url = uri("https://storage.googleapis.com/generative-ai-android/maven/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "OrganizacionEsperanzas"
include(":app")
