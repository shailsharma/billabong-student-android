package in.securelearning.lil.android.player.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import in.securelearning.lil.android.app.databinding.LayoutSampleWebTextBinding;

public class SampleWebTextActivity extends AppCompatActivity {

    LayoutSampleWebTextBinding mBinding;

    public static Intent getStartIntent(Context context) {

        return new Intent(context, SampleWebTextActivity.class);
    }

    TextView htmlTextViewRemote;

    String htmlStringRemote = "<p>The fractions&nbsp;<img  resource-type=\"image\" src=\"https://lilcdn.azureedge.net/lil-upload/1565654400000/a56ZDUHFLVWQK-a29ZVk1XGm8ppx.png\" style=\"margin: 5px;\" >,&nbsp;<img  resource-type=\"image\" src=\"https://lilcdn.azureedge.net/lil-upload/1565654400000/a78Zjh9UG1Bym-a88Zl_Ljei121z.png\" style=\"margin: 5px;\" />are equivalent fractions.</p>\\n\"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        htmlTextViewRemote = new TextView(this);
        setContentView(htmlTextViewRemote);

        htmlTextViewRemote.setText(Html.fromHtml(htmlStringRemote,
                new Html.ImageGetter() {

                    @Override
                    public Drawable getDrawable(String source) {

                        Toast.makeText(getApplicationContext(), source,
                                Toast.LENGTH_LONG).show();

                        HttpGetDrawableTask httpGetDrawableTask = new HttpGetDrawableTask(
                                htmlTextViewRemote, htmlStringRemote);
                        httpGetDrawableTask.execute(source);


                        return null;
                    }

                }, null));

        htmlTextViewRemote.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public class HttpGetDrawableTask extends AsyncTask<String, Void, Drawable> {

        TextView taskTextView;
        String taskHtmlString;

        HttpGetDrawableTask(TextView v, String s) {
            taskTextView = v;
            taskHtmlString = s;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            Drawable drawable = null;
            URL sourceURL;
            try {
                sourceURL = new URL(params[0]);
                URLConnection urlConnection = sourceURL.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(
                        inputStream);
                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);

                // convert Bitmap to Drawable
                drawable = new BitmapDrawable(getResources(), bm);

                drawable.setBounds(0, 0, 172, 172);


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {

            final Drawable taskDrawable = result;

            if (taskDrawable != null) {
                taskTextView.setText(Html.fromHtml(taskHtmlString,
                        new Html.ImageGetter() {

                            @Override
                            public Drawable getDrawable(String source) {
                                return taskDrawable;
                            }
                        }, null));
            }

        }

    }


}
