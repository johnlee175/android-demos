// IServiceManager.aidl
package com.johnsoft.app.services;

import com.johnsoft.app.services.IServicesCallback;

interface IServicesManager {
    IBinder getService(String serviceName);
    void setServicesCallback(IServicesCallback servicesCallback);
}
