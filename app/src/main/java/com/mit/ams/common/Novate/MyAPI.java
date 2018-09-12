package com.mit.ams.common.Novate;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface MyAPI{
    @GET("{url}")
    Observable<ResponseBody> getLogin(@Path("url") String url,
                                      @QueryMap Map<String, String> maps);
}