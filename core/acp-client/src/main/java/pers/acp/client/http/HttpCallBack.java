package pers.acp.client.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author zhang by 02/07/2019
 * @since JDK 11
 */
public abstract class HttpCallBack implements Callback {

    public void onFailure(Call call, IOException e) {
        onRequestFailure(call, e);
    }

    public void onResponse(Call call, Response response) throws IOException {
        onRequestResponse(call, AcpClient.parseResponseResult(response));
    }

    abstract void onRequestFailure(Call call, IOException e);

    abstract void onRequestResponse(Call call, ResponseResult responseResult);

}
