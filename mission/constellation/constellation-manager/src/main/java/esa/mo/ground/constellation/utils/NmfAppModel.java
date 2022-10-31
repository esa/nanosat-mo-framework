package esa.mo.ground.constellation.utils;

public class NmfAppModel {
    final String name;
    final String description;
    final String category;
    int running;
    int installationCounter;

    public NmfAppModel(String name, String description, String category, boolean running) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.running = (running ? 1 : 0);
        this.installationCounter = 1;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getRunning() {
        return running;
    }

    public int getInstallationCounter() {
        return installationCounter;
    }

    public void increaseInstalledAndRunning() {
        this.running += 1;
        this.installationCounter += 1;
    }

    public void increaseInstalled() {
        this.installationCounter += 1;
    }

    public String getRunningCounter() {
        return this.running + "/" + this.installationCounter;
    }

}
