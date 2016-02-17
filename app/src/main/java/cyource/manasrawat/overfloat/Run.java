package cyource.manasrawat.overfloat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.ArrayList;

public class Run extends Service {

    private WindowManager windowManager;
    private ImageView floatingButton;
    private ImageView removeView;
    private float scale;
    private Spring spring;
    private WindowManager.LayoutParams params;
    private int i;
    private ArrayList<String> packageNames;
    private ArrayList<String> appNames;
    private ScrollView scrollView;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, final int startId) {
        packageNames = intent.getStringArrayListExtra("PACKAGE_NAMES");
        appNames = intent.getStringArrayListExtra("APP_NAMES");
        final int[] position = {0, -20};
        TypedValue typedValue = new TypedValue();
        getApplication().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

        // (VIEW DECLARATIONS) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingButton = new ImageView(this);
        floatingButton.setX(-100);
        floatingButton.animate().translationX(0);
        floatingButton.setImageResource(R.mipmap.floatingbutton);

        removeView = new ImageView(this);
        removeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.accent));
        removeView.setImageResource(R.drawable.ic_close_white_36dp);
        removeView.setVisibility(View.GONE);
        removeView.setPadding(10, 10, 10, 10);
        removeView.setY(-100);
        removeView.setBackgroundResource(typedValue.resourceId);
        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                stopSelf();
            }
        });

        scrollView = new ScrollView(this);


        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (appNames.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setTextSize(20);
            emptyView.setPadding(20, 20, 20, 20);
            emptyView.setText("No Apps Selected");
            emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(emptyView);
        }

        for (i = 0; i < appNames.size(); i++) {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setPadding(20, 20, 20, 20);
            textView.setBackgroundResource(typedValue.resourceId);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            final String a = packageNames.get(i);
            textView.setText(appNames.get(i));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(getPackageManager().getLaunchIntentForPackage(a));
                    gone();
                    resize(1f);
                }
            });

            linearLayout.addView(textView);
        }

        scrollView.setVisibility(View.GONE);
        //}

        // (WINDOWMANAGER PARAMS) {

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = position[0];
        params.y = position[1];
        params.dimAmount = 0.6f;

        WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 480,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params2.gravity = Gravity.CENTER;

        WindowManager.LayoutParams params3 = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 125,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params3.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        //}

        // (REBOUND SPRING COMPONENTS + ONTOUCH LISTENER) {
        SpringSystem springSystem = SpringSystem.create();
        spring = springSystem.createSpring()
                .setSpringConfig(new SpringConfig(180, 20))
                .setCurrentValue(1.0)
                .addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        params.x = (int) (SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue()
                                , 0, 1, 25, position[0]));
                        params.y = (int) (SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue()
                                , 0, 1, 25, position[1]));
                        windowManager.updateViewLayout(floatingButton, params);
                    }
                });

        floatingButton.setOnTouchListener(new View.OnTouchListener() {

            //INTS + FLOATS
            int intX;
            int intY;
            float touchX;
            float touchY;

            //BOOLEANS
            boolean motion = false;
            boolean visible = false;
            boolean drag = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        motion = false;
                        intX = params.x;
                        intY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_UP:
                        if (motion)
                            return true;
                        if (!visible) {
                            floatingButton.getLocationOnScreen(position);
                            params.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                            scrollView.setVisibility(View.VISIBLE);
                            removeView.setVisibility(View.VISIBLE);
                            animation(0, true, true, null);
                            drag = true;
                            scale = 1f - (((float) spring.getCurrentValue()) * 0.2f);
                            spring.setEndValue(0.0);
                        } else {
                            drag = false;
                            scale = 1f;
                            gone();
                        }
                        resize(scale);
                        visible = !visible;
                        windowManager.updateViewLayout(floatingButton, params);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (!drag) {
                            motion = true;
                            params.x = intX + (int) (event.getRawX() - touchX);
                            params.y = intY + (int) (event.getRawY() - touchY);
                            windowManager.updateViewLayout(floatingButton, params);
                            return true;
                        }
                        return false;
                }
                return false;
            }
        });
        //}

        //VIEWS ADDING
        scrollView.addView(linearLayout);
        WindowManager.LayoutParams[] wmlp = {params, params2, params3};
        View[] v = {floatingButton, scrollView, removeView};
        for (int i = 0; i < 3; i++) {
            windowManager.addView(v[i], wmlp[i]);
        }

        return START_STICKY;
    }

    // (SECONDARY METHODS) {
    public void gone() {
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        scrollView.setVisibility(View.GONE);
        animation(-100, true, true, null);
        spring.setEndValue(1.0);
    }

    public void resize(final float scale) {
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                floatingButton.setScaleX(scale);
                floatingButton.setScaleY(scale);
            }
        });
    }

    public void animation(final int to, final boolean vis, boolean trans, final View view) {

        if (trans) {

            removeView.animate().translationY(to).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationCancel(animation);
                    if (vis) {
                        if (to == -100)
                            removeView.setVisibility(View.GONE);
                    } else {
                        windowManager.removeView(removeView);
                        removeView.setOnClickListener(null);
                    }
                }
            });
        } else {
            view.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationCancel(animation);
                    windowManager.removeView(view);
                    view.setOnClickListener(null);
                }
            });

        }
    }
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();
        View[] list = {floatingButton, scrollView};
        for (int i = 0; i <= 1; i++)
            animation(0, false, false, list[i]);
        animation(-100, false, true, null);
    }
}
