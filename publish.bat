@echo off
call gradlew curseforge -Ptr_energy_version=2.3.0
call gradlew curseforge -Ptr_energy_version=3.0.0
call gradlew curseforge -Ptr_energy_version=4.1.0
call gradlew curseforge -Ptr_energy_version=4.2.0
call gradlew curseforge -Ptr_energy_version=5.0.0
call gradlew modrinth -Ptr_energy_version=2.3.0
call gradlew modrinth -Ptr_energy_version=3.0.0
call gradlew modrinth -Ptr_energy_version=4.1.0
call gradlew modrinth -Ptr_energy_version=4.2.0
call gradlew modrinth -Ptr_energy_version=5.0.0
pause
