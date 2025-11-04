# Utilisation de cette versiion du JRE pour l'exécution. 
#FROM eclipse-temurin:21-jre
FROM eclipse-temurin:21-jre-alpine
# Toutes les commandes vont être exécutées dans ce répertoire.
WORKDIR /app
# Le ...SNAPSHOT.jar est copié et renommé en app.jar (convention standard)
COPY target/*.jar app.jar
# Uniquement pour documenter => attention : risque de doublons/oublis si changement dans application.properties.
EXPOSE 8080
# Commande qui sera exécutée automatiquement au démarrage du conteneur
ENTRYPOINT ["java","-jar","/app/app.jar"]
