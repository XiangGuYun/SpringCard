package com.springcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    val ivList = arrayListOf(
            R.id.iv1,R.id.iv2,R.id.iv3,R.id.iv4,R.id.iv5,R.id.iv6,R.id.iv7,R.id.iv8,R.id.iv9,R.id.iv10,
            R.id.iv11,R.id.iv12,R.id.iv13,R.id.iv14,R.id.iv15,R.id.iv16,R.id.iv17,R.id.iv18,R.id.iv19,R.id.iv20,
            R.id.iv21,R.id.iv22,R.id.iv23,R.id.iv24,R.id.iv25,R.id.iv26,R.id.iv27,R.id.iv28,R.id.iv29,R.id.iv30,
            R.id.iv31,R.id.iv32,R.id.iv33,R.id.iv34,R.id.iv35,R.id.iv36,R.id.iv37,R.id.iv38,R.id.iv39,R.id.iv40,
            R.id.iv41,R.id.iv42,R.id.iv43,R.id.iv44,R.id.iv45,R.id.iv46,R.id.iv47,R.id.iv48,R.id.iv49,R.id.iv50
    )

    val imgList = arrayListOf(
            R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,R.mipmap.img4,R.mipmap.img5,R.mipmap.img6,R.mipmap.img7,R.mipmap.img8,R.mipmap.img9,R.mipmap.img10,
            R.mipmap.img11,R.mipmap.img12,R.mipmap.img13,R.mipmap.img14,R.mipmap.img15,R.mipmap.img16,R.mipmap.img17,R.mipmap.img18,R.mipmap.img19,R.mipmap.img20,
            R.mipmap.img21,R.mipmap.img22,R.mipmap.img23,R.mipmap.img24,R.mipmap.img25,R.mipmap.img26,R.mipmap.img27,R.mipmap.img28,R.mipmap.img29,R.mipmap.img30,
            R.mipmap.img31,R.mipmap.img32,R.mipmap.img33,R.mipmap.img34,R.mipmap.img35,R.mipmap.img36,R.mipmap.img37,R.mipmap.img38,R.mipmap.img39,R.mipmap.img40,
            R.mipmap.img41,R.mipmap.img42,R.mipmap.img43,R.mipmap.img44,R.mipmap.img45,R.mipmap.img46,R.mipmap.img47,R.mipmap.img48,R.mipmap.img49,R.mipmap.img50
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val bmp = decodeBitmap(this, R.mipmap.bg, resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels)
        val bmpCard = decodeBitmap(this, R.mipmap.big_card, dip2px(this,200f), dip2px(this,300f))
        ivBg.setImageBitmap(bmp)
        ivCard.setImageBitmap(bmpCard)

        ivList.forEach {
            findViewById<ImageView>(it).setOnClickListener {
                startRotation()
                (it as ImageView).setImageResource(R.mipmap.ic_launcher)
                it.setOnClickListener(null)
            }

        }
    }

    lateinit var rotation:_3D

    private fun startRotation() {
        // 计算中心点
        val centerX = ivCard.width / 2.0f
        val centerY = ivCard.height / 2.0f
        rotation = _3D(0f, 360f)
        rotation.initialize(centerX.toInt(), centerY.toInt(), resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        rotation.duration = 2500L
        rotation.fillAfter = true
        //rotation.setInterpolator(new AccelerateInterpolator());
        //匀速旋转
        rotation.interpolator = AccelerateDecelerateInterpolator()
        //设置监听
        rotation.setAnimationListener(object :AnimationListener{
            override fun onAnimationEnd(animation: Animation) {
                // TODO Auto-generated method stub
                val random = Random(System.currentTimeMillis())
                val randomIndex = random.nextInt(0, imgList.size)
                ivCard.setImageBitmap( decodeBitmap(this@MainActivity,
                        imgList[randomIndex], dip2px(this@MainActivity,200f),
                        dip2px(this@MainActivity,300f)))
                imgList.removeAt(randomIndex)
            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub
            }

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub

            }
        })
        ivCard.startAnimation(rotation)
    }

    fun decodeBitmap(ctx: Context, id: Int, targetW: Int, targetH: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true//设置只解码图片的边框（宽高）数据，只为测出采样率
        options.inPreferredConfig = Bitmap.Config.RGB_565//设置图片像素格式的首选配置
        BitmapFactory.decodeResource(ctx.resources, id, options)//预加载
        //获取图片的原始宽高
        val originalW = options.outWidth
        val originalH = options.outHeight
        //设置采样大小
        options.inSampleSize = getSimpleSize(originalW, originalH, targetW, targetH)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(ctx.resources, id, options)
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * 计算采样率
     */
    private fun getSimpleSize(originalW: Int, originalH: Int, targetW: Int, targetH: Int): Int {
        var sampleSize = 1
        if (originalW > originalH && originalW > targetW) {//以宽度来计算采样值
            sampleSize = originalW / targetW
        } else if (originalW < originalH && originalH > targetH) {
            sampleSize = originalH / targetH
        }
        if (sampleSize <= 0) {
            sampleSize = 1
        }
        return sampleSize
    }

}
