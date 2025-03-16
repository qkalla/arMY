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
        jcenter() // Support for older dependencies
        maven { url = uri("https://maven.google.com") } // Google repository
        maven { url = uri("https://maven.yandex.ru") } // Yandex repository
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // Allow project-level repositories
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://maven.yandex.ru") }
    }
}

rootProject.name = "arMY"
include(":app")
