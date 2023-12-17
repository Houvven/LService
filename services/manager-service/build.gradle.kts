val default = "default"

configurations.maybeCreate(default)
artifacts.add(default, file("manager-service-release.aar"))