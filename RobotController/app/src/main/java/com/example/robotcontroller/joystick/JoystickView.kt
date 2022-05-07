package com.example.robotcontroller.joystick

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.compose.ui.graphics.RadialGradientShader
import com.example.robotcontroller.R
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

class JoystickView : View, Runnable {


    private var mButtonStickToBorder: Boolean
    private var borderColor: Int
    private var buttonColor: Int
    private var borderWidth: Int
    private var mBorderAlpha: Int

    private var mBackgroundSizeRatio: Float
    private var mButtonSizeRatio: Float
    private var mBackgroundRadius: Float = 0.0f

    private var mPaintCircleButton: Paint? = null
    private var mPaintCircleBorder: Paint? = null
    private var mPaintBackground: Paint? = null

    private var mPosX = 0
    private var mPosY = 0
    private var mCenterX = 0
    private var mCenterY = 0
    private var mFixedCenterX = 0
    private var mFixedCenterY = 0
    private var mFixedCenter = false

    private var mButtonDirection = 0
    private var mButtonRadius = 0
    private var mBorderRadius = 0
    private var mAutoReCenterButton = true
    private val mCallback: OnMoveListener? = null

    private val DEFAULT_LOOP_INTERVAL = 50 // in milliseconds
    private val MOVE_TOLERANCE = 10
    private val DEFAULT_SIZE = 200
    private val DEFAULT_WIDTH_BORDER = 3F
    private val DEFAULT_COLOR_BUTTON = Color.RED
    private val DEFAULT_COLOR_BORDER = Color.WHITE
    private val DEFAULT_ALPHA_BORDER = 255
    private val DEFAULT_BACKGROUND_COLOR = Color.DKGRAY
    private val DEFAULT_FIXED_CENTER = true
    private val DEFAULT_AUTO_RECENTER_BUTTON = true
    private val DEFAULT_BUTTON_STICK_TO_BORDER = false
    private val BUTTON_DIRECTION_BOTH = 0

    private var radialGradient: RadialGradient? = null
    private val mOnMultipleLongPressListener: OnMultipleLongPressListener? = null
    private val mHandlerMultipleLongPress = Handler()
    private var mRunnableMultipleLongPress: Runnable? = null
    private var mMoveTolerance = 0

    private val mLoopInterval: Long = DEFAULT_LOOP_INTERVAL.toLong()
    private var mThread = Thread(this)

    private var mmFriHandler: MicroFRIHandler

    constructor(context: Context?, attrs: AttributeSet? = null, mFriHandler: MicroFRIHandler) : super(context, attrs) {
        initPosition()

        mmFriHandler = mFriHandler

        val styledAttributes = context!!.theme.obtainStyledAttributes(
            attrs,
            R.styleable.JoystickView,
            0, 0
        )
        var backgroundColor: Int;
        try {
            buttonColor = styledAttributes.getColor(
                R.styleable.JoystickView_JV_buttonColor,
                DEFAULT_COLOR_BUTTON
            )
            borderColor = styledAttributes.getColor(
                R.styleable.JoystickView_JV_borderColor,
                DEFAULT_COLOR_BORDER
            )
            mBorderAlpha = styledAttributes.getInt(
                R.styleable.JoystickView_JV_borderAlpha,
                DEFAULT_ALPHA_BORDER
            )

            backgroundColor = styledAttributes.getColor(
                R.styleable.JoystickView_JV_backgroundColor,
                DEFAULT_BACKGROUND_COLOR
            )
            borderWidth = styledAttributes.getDimensionPixelSize(
                R.styleable.JoystickView_JV_borderWidth,
                DEFAULT_WIDTH_BORDER.toInt()
            )
            mFixedCenter = styledAttributes.getBoolean(
                R.styleable.JoystickView_JV_fixedCenter,
                DEFAULT_FIXED_CENTER
            )
            mAutoReCenterButton = styledAttributes.getBoolean(
                R.styleable.JoystickView_JV_autoReCenterButton,
                DEFAULT_AUTO_RECENTER_BUTTON
            )
            mButtonStickToBorder = styledAttributes.getBoolean(
                R.styleable.JoystickView_JV_buttonStickToBorder,
                DEFAULT_BUTTON_STICK_TO_BORDER
            )
            mButtonSizeRatio =
                styledAttributes.getFraction(
                    R.styleable.JoystickView_JV_buttonSizeRatio,
                    1,
                    1,
                    0.25f
                )
            mBackgroundSizeRatio =
                styledAttributes.getFraction(
                                R.styleable.JoystickView_JV_backgroundSizeRatio,
                                1,
                                1,
                                0.75f
                            )
            mButtonDirection = styledAttributes.getInteger(
                R.styleable.JoystickView_JV_buttonDirection,
                BUTTON_DIRECTION_BOTH
            )
        } finally {
            styledAttributes.recycle()
        }

        // Initialize the drawing according to attributes
        mPaintCircleButton = Paint()
        mPaintCircleButton!!.isAntiAlias = true
        mPaintCircleButton!!.color = buttonColor
        mPaintCircleButton!!.style = Paint.Style.FILL
        mPaintCircleButton!!.shader = radialGradient;

        mPaintCircleBorder = Paint()
        mPaintCircleBorder!!.isAntiAlias = true
        mPaintCircleBorder!!.color = borderColor
        mPaintCircleBorder!!.style = Paint.Style.STROKE
        mPaintCircleBorder!!.strokeWidth = borderWidth.toFloat()
        mPaintCircleBorder!!.alpha = if (borderColor != Color.TRANSPARENT) mBorderAlpha else borderColor

        mPaintBackground = Paint()
        mPaintBackground!!.isAntiAlias = true
        mPaintBackground!!.color = backgroundColor
        mPaintBackground!!.style = Paint.Style.FILL

        // Init Runnable for MultiLongPress
        mRunnableMultipleLongPress =
            Runnable {
                mOnMultipleLongPressListener?.onMultipleLongPress()
            }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        initPosition()

        // radius based on smallest size : height OR width
        val d = min(w, h)
        mButtonRadius = (d / 2 * mButtonSizeRatio).toInt()
        mBorderRadius = (d / 2 * mBackgroundSizeRatio).toInt()
        mBackgroundRadius = mBorderRadius - mPaintCircleBorder!!.strokeWidth / 2

        radialGradient = createRadialGradient((d / 2 * mButtonSizeRatio))
    }

    private fun createRadialGradient(radius: Float) : RadialGradient {
        return RadialGradient(
            (mPosX + mFixedCenterX - mCenterX).toFloat(),
            (mPosY + mFixedCenterY - mCenterY).toFloat(),
            100F,
            intArrayOf(
                Color.parseColor("#E30022"),
                Color.parseColor("#F7E7CE"),
                Color.parseColor("#FFF600")
            ),
            null,
            Shader.TileMode.MIRROR // shader titling mode
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // setting the measured values to resize the view to a certain width and height
        val d = min(measure(widthMeasureSpec), measure(heightMeasureSpec))
        setMeasuredDimension(d, d)
    }


    private fun measure(measureSpec: Int): Int {
        return if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            // if no bounds are specified return a default size (200)
            DEFAULT_SIZE
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            MeasureSpec.getSize(measureSpec)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            mPosY = if (mButtonDirection < 0) mCenterY else event.y.toInt()
        }
        if (event != null) {
            mPosX = if (mButtonDirection > 0) mCenterX else event.x.toInt()
        }

        if (event!!.action == MotionEvent.ACTION_UP) {
            mThread.interrupt()

            if (mAutoReCenterButton) {
                resetButtonPosition()
                mCallback?.onMove(getAngle(), getStrength())
            }
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mThread.isAlive) {
                mThread.interrupt()
            }
            mThread = Thread(this)
            mThread.start()
            mCallback?.onMove(getAngle(), getStrength())
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN ->                 // when the first touch occurs we update the center (if set to auto-defined center)
                if (!mFixedCenter) {
                    mCenterX = mPosX
                    mCenterY = mPosY
                }
            MotionEvent.ACTION_POINTER_DOWN -> {

                if (event.pointerCount == 2) {
                    mHandlerMultipleLongPress.postDelayed(
                        mRunnableMultipleLongPress!!,
                        (ViewConfiguration.getLongPressTimeout() * 2).toLong()
                    )
                    mMoveTolerance = MOVE_TOLERANCE
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mMoveTolerance--
                if (mMoveTolerance == 0) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress!!)
                }

                mmFriHandler.sendMoveFrame(getAngle(), getStrength());
            }
            MotionEvent.ACTION_POINTER_UP -> {

                // when the last multiple touch is released
                if (event.pointerCount == 2) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress!!)
                }
            }
        }

        val abs = sqrt(
            ((mPosX - mCenterX) * (mPosX - mCenterX)
                    + (mPosY - mCenterY) * (mPosY - mCenterY)).toDouble()
        )

        if (abs > mBorderRadius && abs != 0.0) {
            mPosX = (((mPosX - mCenterX) * mBorderRadius / abs + mCenterX).toInt())
            mPosY = (((mPosY - mCenterY) * mBorderRadius / abs + mCenterY).toInt())
        }

        if (!mAutoReCenterButton) {
            // Now update the last strength and angle if not reset to center
            mCallback?.onMove(getAngle(), getStrength())
        }

        // to force a new draw
        invalidate()

        return true;
        //return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the background
        mPaintBackground?.let {
            canvas.drawCircle(
                mFixedCenterX.toFloat(),
                mFixedCenterY.toFloat(),
                mBackgroundRadius,
                it
            )
        }

        // Draw the circle border
        mPaintCircleBorder?.let {
            canvas.drawCircle(
                mFixedCenterX.toFloat(),
                mFixedCenterY.toFloat(),
                mBorderRadius.toFloat(),
                it
            )
        }

        mPaintCircleButton?.let {
            canvas.drawCircle(
                (mPosX + mFixedCenterX - mCenterX).toFloat(),
                (mPosY + mFixedCenterY - mCenterY).toFloat(),
                mButtonRadius.toFloat(),
                it
            )
        }
    }

    override fun run() {
        while (!Thread.interrupted()) {
            post(Runnable {
                mCallback?.onMove(getAngle(), getStrength())
            })

            try {
                Thread.sleep(mLoopInterval)
            } catch (e: InterruptedException) {
                break
            }
        }
    }



    private fun initPosition() {
        // get the center of view to position circle
        mPosX = getScreenWidth() / 2
        mCenterX = mPosX
        mFixedCenterX = mCenterX
        mPosY = getScreenHeight() / 2
        mCenterY = mPosY
        mFixedCenterY = mCenterY
    }

    private fun resetButtonPosition() {
        mPosX = mCenterX
        mPosY = mCenterY
    }

    private fun getAngle(): Int {
        val angle = Math.toDegrees(atan2(
            (mCenterY - mPosY).toDouble(),
            (mPosX - mCenterX).toDouble())).toInt()
        return if (angle < 0) angle + 360 else angle // make it as a regular counter-clock protractor
    }

    private fun getStrength(): Int {
        return (100 * sqrt(
            ((mPosX - mCenterX)
                    * (mPosX - mCenterX) + (mPosY - mCenterY)
                    * (mPosY - mCenterY)).toDouble()
        ) / mBorderRadius).toInt()
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}