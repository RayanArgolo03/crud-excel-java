package controllers;

import services.FolderService;

public class FolderController extends FileControllerAbstract {
    private FolderService folderService;
    public FolderController(FolderService folderService) {
        super();
        this.folderService = folderService;
    }

    public FolderService getFolderService() {
        return folderService;
    }

}
