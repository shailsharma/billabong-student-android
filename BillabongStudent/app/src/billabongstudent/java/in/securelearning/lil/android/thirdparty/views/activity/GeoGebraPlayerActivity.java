package in.securelearning.lil.android.thirdparty.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.apache.cordova.CordovaActivity;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.utils.GeneralUtils;
import in.securelearning.lil.android.syncadapter.utils.ConstantUtil;

public class GeoGebraPlayerActivity extends CordovaActivity {

    private LocalWebServer mLocalWebServer;
    private static final String MATERIAL_ID = "materialId";
    private static final String INSTRUCTION = "instruction";
    private String mMaterialId;
    private String mInstruction;

    public static Intent getStartIntent(Context context, String materialId, String instruction) {
        Intent intent = new Intent(context, GeoGebraPlayerActivity.class);
        intent.putExtra(MATERIAL_ID, materialId);
        intent.putExtra(INSTRUCTION, instruction);
        return intent;
    }

    @Override
    protected void createViews() {
        super.createViews();
        FrameLayout layout = (FrameLayout) appView.getView().getParent();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(56, 56);
        params.gravity = Gravity.END | Gravity.TOP;
        params.setMargins(16, 16, 16, 16);

        ImageView imageViewHelp = new ImageView(layout.getContext());
        imageViewHelp.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_solid_primary));
        imageViewHelp.setImageResource(R.drawable.info_w);
        imageViewHelp.setPadding(4, 4, 4, 4);
        imageViewHelp.setElevation(4f);
        imageViewHelp.setLayoutParams(params);
        imageViewHelp.setVisibility(View.GONE);

        layout.addView(imageViewHelp);

        if (!TextUtils.isEmpty(mInstruction)) {
            imageViewHelp.setVisibility(View.VISIBLE);
            imageViewHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelpDialog(mInstruction);
                }
            });
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorTransparentGrey));
        handleIntent();

    }


    /*To show help dialog at the start of activity if instruction available.*/
    private void showHelpDialog(String instruction) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(instruction)
                .setPositiveButton(getString(R.string.got_it), null)
                .show();
        TextView textView = dialog.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mLocalWebServer != null) {
            mLocalWebServer.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocalWebServer != null) {
            mLocalWebServer.stop();
        }
    }

    private void handleIntent() {
        if (getIntent() != null) {
            mMaterialId = getIntent().getStringExtra(MATERIAL_ID);
            mInstruction = getIntent().getStringExtra(INSTRUCTION);


            if (!TextUtils.isEmpty(mInstruction)) {
                showHelpDialog(mInstruction);
            }


            startServerAndPlayGeoGebra();
        } else {
            closeActivity();
        }
    }


    private void startServerAndPlayGeoGebra() {
        try {
            mLocalWebServer = new LocalWebServer(ConstantUtil.GEO_GEBRA_LOCAL_PORT);//any port number from constant utils
            mLocalWebServer.start();

            loadUrl("http://localhost:" + mLocalWebServer.getListeningPort() + "/geoGebraPlayer");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class LocalWebServer extends NanoHTTPD {

        private LocalWebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            if (uri.equals("/geoGebraPlayer")) {
                String response = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "  <head>\n" +
                        "    <meta charset=\"UTF-8\" />\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />\n" +
                        "    <title>Document</title>\n" +
                        "    <script src=\"https://www.geogebra.org/apps/deployggb.js\"></script>\n" +
                        "  </head>\n" +
                        "  <body\n" +
                        "    style=\"height: 100vh; \n" +
                        "    width: 100vw; \n" +
                        "    display: flex; \n" +
                        "    justify-content: center; \n" +
                        "    align-items: center; \n" +
                        "    margin: auto;\"\n" +
                        "  >\n" +
                        "    <div id=\"ggb-element\"></div>\n" +
                        "\n" +
                        "    <script>\n" +
                        "      var ggbApp = new GGBApplet(\n" +
                        "        {\n" +
                        "        //   appName: 'graphing', //graphing, geometry, 3d\n" +
                        "        //   width: 1280,\n" +
                        "        //   height: 1024,\n" +
                        "        //   country: 'IN',\n" +
                        "        //   allowUpscale: true,\n" +
                        "        //   showZoomButtons: true,\n" +
                        "          material_id: '" + mMaterialId + "'\n" +
                        "        },\n" +
                        "        true\n" +
                        "      );\n" +
                        "      window.addEventListener('load', function() {\n" +
                        "        ggbApp.inject('ggb-element');\n" +
                        "      });\n" +
                        "    </script>\n" +
                        "  </body>\n" +
                        "</html>";
                return newFixedLengthResponse(response);
            }
            return null;
        }

    }

    /*To close/finish activity with toast.*/
    private void closeActivity() {
        finish();
        GeneralUtils.showToastShort(getBaseContext(), getString(R.string.error_something_went_wrong));
    }
}
