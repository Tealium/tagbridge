package com.tealium.googledfp;


import android.view.Gravity;
import android.widget.FrameLayout;

enum Anchor {
    TOP(Gravity.CENTER_HORIZONTAL | Gravity.TOP),
    BOTTOM(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

    final int gravity;

    Anchor(int gravity) {
        this.gravity = gravity;
    }

    public FrameLayout.LayoutParams createFrameLayoutLayoutParams() {
        final FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        fllp.gravity = this.gravity;

        return fllp;
    }
}
