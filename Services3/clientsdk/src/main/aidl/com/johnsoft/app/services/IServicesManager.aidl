// IServiceManager.aidl
package com.johnsoft.app.services;

interface IServicesManager {
    IBinder getService(String serviceName);
}
