make desktop, jar, sincronizar:

desktop:
	./gradlew lwjgl3:run --warning-mode all

jar:
	./gradlew lwjgl3:dist

sincronizar:
	./gradlew clean build