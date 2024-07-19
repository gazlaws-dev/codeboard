package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.gazlaws.codeboard.KeyboardPreferences;
import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;
import com.gazlaws.codeboard.theme.UiTheme;

import java.util.Timer;
import java.util.TimerTask;

public class KeyboardButtonView extends View {

    private static final String TAG = "KeyboardButtonView";
    private final Key key;
    private final KeyboardView.OnKeyboardActionListener inputService;
    private final KeyboardPreferences keyboardPreferences;
    private final UiTheme uiTheme;
    private Timer timer;
    private String currentLabel = null;
    private boolean isPressed = false;

    public KeyboardButtonView(Context context, Key key, KeyboardView.OnKeyboardActionListener inputService, UiTheme uiTheme, KeyboardPreferences keyboardPreferences) {
        super(context);
        this.inputService = inputService;
        this.key = key;
        this.uiTheme = uiTheme;
        this.currentLabel = key.info.label;
        this.keyboardPreferences = keyboardPreferences;

        // Enable shadow
        this.setOutlineProvider(ViewOutlineProvider.BOUNDS);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // Required for blur effect
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onPress();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: 
      // ACTION_CANCEL is used to handle the case where the user moves their finger off the button
                onRelease();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        Box box = key.box;
        int w = r - l;
        int h = b - t;
        int left = (int) (l + w * box.getLeft());
        int right = (int) (l + w * box.getRight());
        int top = (int) (t + h * box.getTop());
        int bottom = (int) (t + h * box.getBottom());
        super.layout(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        drawButtonBody(canvas);
        drawButtonContent(canvas);
        super.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        autoReleaseIfPressed();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        autoReleaseIfPressed();
    }

    private void drawButtonContent(Canvas canvas) {
        if (currentLabel != null) {
            float x = this.getWidth() / 2;
            float y = this.getHeight() / 2 + uiTheme.fontHeight / 3;
            canvas.drawText(currentLabel, x, y, uiTheme.foregroundPaint);
        }

        if (key.info.icon != null) {
            Drawable d = key.info.icon;
            d.setTint(uiTheme.foregroundPaint.getColor());

            int padding = (int) uiTheme.buttonBodyPadding * 2;
            int top;
            int left;
            int squareSize;
            if (this.getWidth() > this.getHeight()) {
                top = 2 * padding;
                squareSize = (this.getHeight() / 2) - top;
                left = (this.getWidth() / 2) - squareSize;
            } else {
                left = 2 * padding;
                squareSize = this.getWidth() / 2 - (left);
                top = this.getHeight() / 2 - squareSize;
            }
            int right = left + (squareSize * 2);
            int bottom = top + (squareSize * 2);
            d.setBounds(left, top, right, bottom);
            d.draw(canvas);
        }
    }

    private void onPress() {
        isPressed = true;
        if (key.info.code != 0) {
            inputService.onPress(key.info.code);
        }
        if (key.info.isRepeatable) {
            startRepeating();
        }
        submitKeyEvent();
        animatePress();
    }

    private void onRelease() {
        isPressed = false;
//      NOTE: If the arrow keys move out of the input view, the onRelease is never called
        if (key.info.code != 0) {
            inputService.onRelease(key.info.code);
        }
        if (key.info.isRepeatable) {
            stopRepeating();
        }
        animateRelease();
    }

    private void submitKeyEvent() {
        if (key.info.code != 0) {
            inputService.onKey(key.info.code, null);
        }
        if (this.key.info.outputText != null) {
            inputService.onText(this.key.info.outputText);
        }
    }

    private void autoReleaseIfPressed() {
        if (isPressed) {
            onRelease();
        }
    }

    private void stopRepeating() {
    //  NOTE: This method is synchronized means that only one thread can access it at a time
        synchronized (this) {
            if (timer == null) {
                return;
            }
            timer.cancel();
            timer = null;
        }
    }

    private void startRepeating() {
        synchronized (this) {
            if (timer != null) {
                stopRepeating();
                return;
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    submitKeyEvent();
                }
            }, 200, 70);
        }
    }

    private void animatePress(){
        if (uiTheme.enablePreview){
            this.setTranslationY(-200.0f);
            this.setScaleX(1.2f);
            this.setScaleY(1.2f);
            this.setElevation(21.0f);
        } else {
            this.setAlpha(.1f);
        }
    }

    private void animateRelease() {
        if (uiTheme.enablePreview){
            this.setTranslationY(0.0f);
            this.setScaleX(1.0f);
            this.setScaleY(1.0f);
            this.setElevation(0.0f);
        } else {
            this.animate().alpha(1.0f).setDuration(400);
        }
    }

    private void drawButtonBody(Canvas canvas) {
        float left = uiTheme.buttonBodyPadding;
        float top = uiTheme.buttonBodyPadding;
        float right = this.getWidth() - uiTheme.buttonBodyPadding;
        float bottom = this.getHeight() - uiTheme.buttonBodyPadding;

        Paint paint = uiTheme.buttonBodyPaint;

        // Calculate alpha based on button transparency from preferences
        int alpha = (int) (255 * keyboardPreferences.getButtonTransparency());
        paint.setAlpha(alpha);

        // Check if custom button color is enabled
        if (keyboardPreferences.isCustomButtonColorEnabled()) {
            int customColor = keyboardPreferences.getCustomButtonColor();
            paint.setColor(customColor);
            paint.setShader(null); // Clear any previous shader
        } else {
            if (keyboardPreferences.isGradientEnabled()) {
                int startColor = keyboardPreferences.getGradientStartColor();
                int endColor = keyboardPreferences.getGradientEndColor();
                Shader shader = new LinearGradient(left, top, right, bottom, startColor, endColor, Shader.TileMode.CLAMP);
                paint.setShader(shader);
            } else {
                paint.setShader(null); // Clear any previous shader
                paint.setColor(uiTheme.buttonBodyColor); // Default color if no gradient
            }
        }

        if (keyboardPreferences.isButtonBlurEffectEnabled()) {
            paint.setMaskFilter(new BlurMaskFilter(uiTheme.defaultBlurRadius, BlurMaskFilter.Blur.NORMAL));
        } else {
            paint.setMaskFilter(null);
        }

        // Draw rounded rectangle button body
        canvas.drawRoundRect(left, top, right, bottom, uiTheme.buttonBodyBorderRadius, uiTheme.buttonBodyBorderRadius, paint);
    }

    // Modifier methods

    public void applyShiftModifier(boolean shiftPressed) {
        if (this.key.info.onShiftLabel != null){
            String nextLabel = shiftPressed
                    ? this.key.info.onShiftLabel
                    : this.key.info.label;
            setCurrentLabel(nextLabel);
        }
    }

    public void applyCtrlModifier(boolean ctrlPressed) {
        if (this.key.info.onCtrlLabel != null){
            String nextLabel = ctrlPressed
                    ? this.key.info.onCtrlLabel
                    : this.key.info.label;
            setCurrentLabel(nextLabel);
        }
    }

    public void setCurrentLabel(String label) {
        this.currentLabel = label;
        invalidate();
    }
}
