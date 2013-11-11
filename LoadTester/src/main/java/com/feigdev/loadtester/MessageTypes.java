package com.feigdev.loadtester;

/**
 * Created by ejf3 on 11/10/13.
 */
public class MessageTypes {
    static class CpuStatus {
        final String status;
        CpuStatus(String status){
            this.status = status;
        }
        String getStatus(){
            return status;
        }
    }
    static class RunningStatus {

    }

    static class RamStatus {
        final String status;
        RamStatus(String status){
            this.status = status;
        }
        String getStatus(){
            return status;
        }
    }
}
