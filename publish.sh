#!/bin/sh
./gradlew curseforge -Ptr_energy_version=2.3.0
./gradlew curseforge -Ptr_energy_version=3.0.0
./gradlew curseforge -Ptr_energy_version=4.1.0
./gradlew curseforge -Ptr_energy_version=4.2.0
./gradlew modrinth -Ptr_energy_version=2.3.0
./gradlew modrinth -Ptr_energy_version=3.0.0
./gradlew modrinth -Ptr_energy_version=4.1.0
./gradlew modrinth -Ptr_energy_version=4.2.0
read -p "Press any key to exit... " -n1 -s
