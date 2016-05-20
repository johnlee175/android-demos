package com.johnsoft.app.services;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public interface ServicesInterface extends IInterface {
    abstract class Stub extends Binder implements ServicesInterface {
        private static final String DESCRIPTOR = "com.johnsoft.app.services.ServicesInterface";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static ServicesInterface asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            final IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof ServicesInterface))) {
                return ((ServicesInterface) iin);
            }
            return new ServicesInterface.Stub.Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_request: {
                    data.enforceInterface(DESCRIPTOR);
                    final String _arg0 = data.readString();
                    final Bundle _arg1;
                    if (data.readInt() != 0) {
                        _arg1 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    final int _result = this.request(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    if ((_arg1 != null)) {
                        reply.writeInt(1);
                        _arg1.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements ServicesInterface {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public int request(String url, Bundle bundle) throws RemoteException {
                final Parcel _data = Parcel.obtain();
                final Parcel _reply = Parcel.obtain();
                final int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    if ((bundle != null)) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_request, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                    if (bundle != null && _reply.readInt() != 0) {
                        bundle.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_request = IBinder.FIRST_CALL_TRANSACTION;
    }

    int request(String url, Bundle bundle) throws RemoteException;
}
