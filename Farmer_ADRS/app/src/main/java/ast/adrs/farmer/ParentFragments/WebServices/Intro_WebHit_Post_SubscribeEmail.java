package ast.adrs.farmer.ParentFragments.WebServices;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.nio.charset.StandardCharsets;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.Utils.ApiMethod;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IWebCallback;
import ast.adrs.farmer.Utils.RModel_Message;
import cz.msebera.android.httpclient.Header;


/**
 * Created by bilalahmed on 04/05/2018.
 * bilalahmed.cs@live.com
 */

public class Intro_WebHit_Post_SubscribeEmail {

    public static RModel_Message responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void postSubscribe(final IWebCallback iWebCallback, final String _email) {

        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.subscribe;
        RequestParams params = new RequestParams();
        params.put("email", _email);

        mClient.addHeader(ApiMethod.HEADER.Lang, AppConfig.getInstance().loadDefLanguage());
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);
        mClient.post(myUrl, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, RModel_Message.class);

                            switch (statusCode) {

                                case AppConstt.ServerStatus.OK:
                                    iWebCallback.onWebResult(true, responseObject.getMessage());
                                    break;

                                default:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {

                        switch (statusCode) {
                            case AppConstt.ServerStatus.NETWORK_ERROR:
                                iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                break;

                            case AppConstt.ServerStatus.FORBIDDEN:
                            case AppConstt.ServerStatus.DATABASE_NOT_FOUND:
                            default:
                                AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                break;
                        }
                    }
                }

        );
    }

}
