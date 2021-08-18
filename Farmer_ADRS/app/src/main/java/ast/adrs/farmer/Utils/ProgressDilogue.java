package ast.adrs.farmer.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ast.adrs.farmer.R;

public class ProgressDilogue {
    CustomIOSTransparentProgressDialog iOSProgressDialogObj;
    String textColorOfMessage = null;

    public void setTextColorOfMessage(String textColorOfMessage) {
        this.textColorOfMessage = textColorOfMessage;
    }

    public void startiOSLoader(Context context, int image_for_rotation_resource_id, String text, boolean isRequiredTransparent) {


        if (context != null) {
            this.iOSProgressDialogObj = new CustomIOSTransparentProgressDialog(context, image_for_rotation_resource_id);
            TextView viewById = this.iOSProgressDialogObj.findViewById(R.id.progressDialogMessageText);
            ImageView imvLoader = this.iOSProgressDialogObj.findViewById(R.id.progressDialogImage);
            viewById.setText("" + text);
            viewById.setVisibility(View.GONE);

            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            rotate.setDuration(900);
            rotate.setRepeatCount(Animation.INFINITE);
            imvLoader.startAnimation(rotate);

            if (textColorOfMessage != null) {
                viewById.setTextColor(Integer.parseInt(textColorOfMessage));
            }

//        if (isRequiredTransparent) {
//            LinearLayout mainLayout = (LinearLayout) this.iOSProgressDialogObj.findViewById(R.id.progressDialogMainLayout);
//            mainLayout.setBackgroundColor(Color.TRANSPARENT);
//        }
            this.iOSProgressDialogObj.show();
        }
    }

    public void stopiOSLoader() {
       /* if (iOSProgressDialogObj != null) {
            if (iOSProgressDialogObj.isShowing()) {
                iOSProgressDialogObj.dismiss();
                iOSProgressDialogObj = null;
            }
        }*/

        try {
            if ((this.iOSProgressDialogObj != null)) {
                if (this.iOSProgressDialogObj.isShowing()) {
                    this.iOSProgressDialogObj.dismiss();
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            // Handle or log or ignore
        } catch (final Exception e) {
            e.printStackTrace();
            // Handle or log or ignore
        } finally {
            this.iOSProgressDialogObj = null;
        }
        // kProgressHUDProgress.dismiss();
    }

    public class CustomIOSTransparentProgressDialog extends Dialog {

        @SuppressWarnings("unused")
        private ImageView iv;

        /**
         * Constructor
         *
         * @param context           The Context from which the dialog is called
         * @param resourceIdOfImage the ID for the waiting image to be shown
         * @return none
         * @since 2014-07-28
         */
        public CustomIOSTransparentProgressDialog(Context context, int resourceIdOfImage) {
            //super(context, R.style.TransparentProgressDialog);
            super(context, R.style.CustomIOSTransparentProgressDialog);
            WindowManager.LayoutParams wlmp = getWindow().getAttributes();
            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            setContentView(R.layout.custome_progresslayout);
        }

        /**
         * Start the dialog and display it on screen.
         *
         * @return none
         * @since 2014-07-28
         */
        @Override
        public void show() {
            super.show();

        }

        /**
         * Dismiss the dialog
         *
         * @return none
         * @since 2014-07-28
         */
        @Override
        public void dismiss() {
            super.dismiss();
        }
    }

}
