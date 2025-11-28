package com.example.projetdevmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FallingSymbolsView extends View {

    private static class Symbol {
        float x, y, speed, size, rotation, rotationSpeed;
        int color;
        boolean isX;

        Symbol(float x, float y, float speed, float size, int color, boolean isX, float rotation, float rotationSpeed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
            this.color = color;
            this.isX = isX;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
        }
    }

    private List<Symbol> symbols = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean isAnimating = false;
    private Random random = new Random();

    private int[] colors = {
            Color.rgb(231, 76, 60),
            Color.rgb(52, 152, 219),
            Color.rgb(46, 204, 113),
            Color.rgb(155, 89, 182),
            Color.rgb(241, 196, 15),
            Color.rgb(230, 126, 34),
            Color.rgb(26, 188, 156),
            Color.rgb(192, 57, 43),
            Color.rgb(142, 68, 173),
            Color.rgb(39, 174, 96)
    };

    public FallingSymbolsView(Context context) {
        super(context);
        init();
    }

    public FallingSymbolsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FallingSymbolsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setFakeBoldText(true);
    }

    public void startAnimation() {
        if (isAnimating) return;
        isAnimating = true;

        if (symbols.isEmpty()) {
            for (int i = 0; i < 15; i++) {
                addRandomSymbol(random.nextFloat() * getHeight());
            }
        }
        postInvalidate();
    }

    public void stopAnimation() {
        isAnimating = false;
    }

    private void addRandomSymbol(float startY) {
        if (getWidth() == 0 || getHeight() == 0) return;

        Symbol symbol = new Symbol(
                random.nextFloat() * getWidth(),
                startY,
                random.nextFloat() * 3 + 1,
                random.nextFloat() * 80 + 50,
                colors[random.nextInt(colors.length)],
                random.nextBoolean(),
                random.nextFloat() * 360,
                random.nextFloat() * 4 - 2
        );
        symbols.add(symbol);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isAnimating) return;

        List<Symbol> toRemove = new ArrayList<>();

        for (Symbol symbol : symbols) {
            symbol.y += symbol.speed;

            if (symbol.y > getHeight() + 100) {
                toRemove.add(symbol);
                continue;
            }

            canvas.save();
            canvas.translate(symbol.x, symbol.y);
            canvas.rotate(symbol.rotation);

            paint.setTextSize(symbol.size);
            paint.setColor(Color.argb(100, Color.red(symbol.color), Color.green(symbol.color), Color.blue(symbol.color)));

            String text = symbol.isX ? "✕" : "○";
            canvas.drawText(text, 0, symbol.size / 3, paint);

            canvas.restore();

            symbol.rotation += symbol.rotationSpeed;
        }

        symbols.removeAll(toRemove);

        if (symbols.size() < 20 && random.nextFloat() < 0.05f) {
            addRandomSymbol(-100f);
        }

        if (isAnimating) {
            postInvalidateDelayed(16);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        symbols.clear();
        if (isAnimating && w > 0 && h > 0) {
            for (int i = 0; i < 15; i++) {
                addRandomSymbol(random.nextFloat() * h);
            }
        }
    }
}