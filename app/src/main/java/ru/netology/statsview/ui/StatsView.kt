package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return
        val fractionsSum = data.sum() - data[0]
        // Проверим что сумма выводимых частей не превышает сумму для 100%
        if (fractionsSum > data[0]) {
            return
        }
        val total = data.get(0)
        if (total == 0f || colors.isEmpty()) return // Защита от пустых данных или цветов

        // Нормализуем данные внутри метода
        val fractions = data.map { it / total }

        var startFrom = -90f // Начинаем сверху

        paint.color = 0xFFCCCCCC.toInt()
        canvas.drawCircle(center.x, center.y, radius, paint)


        for ((index, fraction) in fractions.withIndex()) {
            if (index > 0) {
                val angle = 360f * fraction

                // Устанавливаем цвет КАЖДОЙ итерации цикла
                paint.color = colors.getOrNull(index % colors.size) ?: randomColor()

                canvas.drawArc(
                    oval,
                    startFrom,
                    angle,
                    false,
                    paint
                )

                startFrom += angle
            }
        }
        // ЗНАЮ КАРЯВО, КОСТЫЛЬНО НО РАБОТАЕТ
        if (fractionsSum > data[0] * 0.975F) {
            //Если Сумма элементов гистограмы болльше 97,5%
            //перевыведем еще раз половинку первого сегмента
            paint.color = colors.getOrNull(1 % colors.size) ?: randomColor()
            canvas.drawArc(oval, startFrom, 90 * fractions[1], false, paint)
        }
        canvas.drawText(
            "%.2f%%".format((fractions.sum() - 1) * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}