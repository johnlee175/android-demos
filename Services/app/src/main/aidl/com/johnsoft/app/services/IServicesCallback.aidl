// IServiceManager.aidl
package com.johnsoft.app.services;

interface IServicesCallback {
    void onServiceUnload(String serviceName);
}
