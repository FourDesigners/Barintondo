package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import it.uniba.di.sms.barintondo.R;

public class ImageSwitcherController {
    public static void setImageSwitcher(final ImageSwitcher imageSwitcher, final EditText editTextPassword, final Context context) {
        final Animation in  = AnimationUtils.loadAnimation(context, R.anim.left_to_right_in);
        final Animation out = AnimationUtils.loadAnimation(context, R.anim.left_to_right_out);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(context);
                return view;
            }
        });
        imageSwitcher.setImageResource(R.drawable.closedeye);
        imageSwitcher.setTag(R.drawable.closedeye);
        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) imageSwitcher.getTag();
                integer = integer == null ? 0 : integer;
                imageSwitcher.setInAnimation(in);
                imageSwitcher.setOutAnimation(out);
                switch(integer) {
                    case R.drawable.openeye:
                        imageSwitcher.setImageResource(R.drawable.closedeye);
                        imageSwitcher.setTag(R.drawable.closedeye);
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageSwitcher.setImageResource(R.drawable.openeye);
                        imageSwitcher.setTag(R.drawable.openeye);
                        editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                }
            }
        });
    }
}
