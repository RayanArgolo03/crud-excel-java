package controllers;

import services.LogService;

public class LogController extends FileControllerAbstract {
    private LogService logService;

    public LogController(LogService logService) {
        super();
        this.logService = logService;
    }

    public LogService getLogService() {
        return logService;
    }

    @Override
    public void setFile(String path) {
        super.setFile(path + "\\logs.txt");
    }


}
