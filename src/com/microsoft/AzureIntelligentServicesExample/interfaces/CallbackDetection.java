package com.microsoft.AzureIntelligentServicesExample.interfaces;


import com.microsoft.projectoxford.face.contract.Face;

public interface CallbackDetection<T> {
    void call(Face[] t);

}
