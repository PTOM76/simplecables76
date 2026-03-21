@echo off
call gradlew build -Ptr_energy_version=2.3.0
call gradlew build -Ptr_energy_version=3.0.0
call gradlew build -Ptr_energy_version=4.1.0
call gradlew build -Ptr_energy_version=4.2.0
pause
