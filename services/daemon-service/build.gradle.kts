val default = "default"

configurations.maybeCreate(default)
artifacts.add(default, file("daemon-service-release.aar"))