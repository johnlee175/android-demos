/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package com.version.increment.patch;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * @author John Kenrinus Lee
 * @version 2016-11-23
 */
public interface Version {
    class PeekData {
        public String product;
        public String flavor;
        public String branch;
        @SerializedName("build_date")
        public String buildDate;
        @SerializedName("apk_md5")
        public String apkMd5;
        @SerializedName("mapping_code")
        public String mappingCode;
        @SerializedName("up_to_date")
        public boolean upToDate;
        @SerializedName("forward_step")
        public boolean forwardStep;
        @SerializedName("prepare_patch")
        public boolean preparePatch;

        @Override
        public String toString() {
            return "PeekData{" +
                    "product='" + product + '\'' +
                    ", flavor='" + flavor + '\'' +
                    ", branch='" + branch + '\'' +
                    ", buildDate='" + buildDate + '\'' +
                    ", apkMd5='" + apkMd5 + '\'' +
                    ", mappingCode='" + mappingCode + '\'' +
                    ", upToDate=" + upToDate +
                    ", forwardStep=" + forwardStep +
                    ", preparePatch=" + preparePatch +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PeekData peekData = (PeekData) o;

            if (upToDate != peekData.upToDate) {
                return false;
            }
            if (forwardStep != peekData.forwardStep) {
                return false;
            }
            if (preparePatch != peekData.preparePatch) {
                return false;
            }
            if (product != null ? !product.equals(peekData.product) : peekData.product != null) {
                return false;
            }
            if (flavor != null ? !flavor.equals(peekData.flavor) : peekData.flavor != null) {
                return false;
            }
            if (branch != null ? !branch.equals(peekData.branch) : peekData.branch != null) {
                return false;
            }
            if (buildDate != null ? !buildDate.equals(peekData.buildDate) : peekData.buildDate != null) {
                return false;
            }
            if (apkMd5 != null ? !apkMd5.equals(peekData.apkMd5) : peekData.apkMd5 != null) {
                return false;
            }
            return mappingCode != null ? mappingCode.equals(peekData.mappingCode) : peekData.mappingCode == null;

        }

        @Override
        public int hashCode() {
            int result = product != null ? product.hashCode() : 0;
            result = 31 * result + (flavor != null ? flavor.hashCode() : 0);
            result = 31 * result + (branch != null ? branch.hashCode() : 0);
            result = 31 * result + (buildDate != null ? buildDate.hashCode() : 0);
            result = 31 * result + (apkMd5 != null ? apkMd5.hashCode() : 0);
            result = 31 * result + (mappingCode != null ? mappingCode.hashCode() : 0);
            result = 31 * result + (upToDate ? 1 : 0);
            result = 31 * result + (forwardStep ? 1 : 0);
            result = 31 * result + (preparePatch ? 1 : 0);
            return result;
        }
    }

    class FetchData {
        public PathMd5 from;
        public PathMd5 to;

        public FetchData() {
        }

        public FetchData(PathMd5 from, PathMd5 to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "FetchData{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }
    }

    class ResponseResult {
        public int code;
        public String message;
        public PathMd5 current;
        public PathMd5 target;
        @SerializedName("patch_size")
        public String patchSize;

        @Override
        public String toString() {
            return "ResponseResult{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", current=" + current +
                    ", target=" + target +
                    ", patchSize='" + patchSize + '\'' +
                    '}';
        }
    }

    class PathMd5 {
        public String path;
        public String md5;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PathMd5 pathMd5 = (PathMd5) o;

            if (path != null ? !path.equals(pathMd5.path) : pathMd5.path != null) {
                return false;
            }
            return md5 != null ? md5.equals(pathMd5.md5) : pathMd5.md5 == null;

        }

        @Override
        public int hashCode() {
            int result = path != null ? path.hashCode() : 0;
            result = 31 * result + (md5 != null ? md5.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "PathMd5{" +
                    "path='" + path + '\'' +
                    ", md5='" + md5 + '\'' +
                    '}';
        }
    }

    @Headers({
            "Content-Type: application/json;charset=utf-8",
    })
    @POST("/peek_version.php")
    Call<ResponseResult> peekVersionInfo(@Body PeekData req);

    @Streaming
    @Headers({
            "Content-Type: application/json;charset=utf-8",
    })
    @POST("/update_version.php")
    Call<ResponseBody> fetchPatch(@Body FetchData req);
}
