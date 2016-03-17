package asia.tatsujin.www2048.client.tool;

/**
 * Created by tatsujin on 2016/3/17.
 */
public interface ResponseListener<T> {
    public void onResponse( T responses );
    public void onError( Throwable throwable );
}