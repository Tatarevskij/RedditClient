package com.example.redditclient.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.redditclient.GlideApp
import com.example.redditclient.R
import com.example.redditclient.databinding.CommentItemLightBinding
import com.example.redditclient.databinding.LinkItemBinding
import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.Data
import org.jsoup.Jsoup
import java.util.*

class CommentItemViewLight(
    applicationContext: Context,
    private val onLoadChildrenClick: (List<String>, View) -> Unit,
    private val onSaveClick: (String, String) -> Unit,
    private val onUnSaveClick: (String) -> Unit,
) : FrameLayout(applicationContext) {
    private var indexForReplyViews: Int? = null
    private var childrenToLoad: List<String>? = null
    private var isSaved = false

    private val binding =
        CommentItemLightBinding.inflate(applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .also { addView(it.root) }

    fun setComment(comment: Comment, userIcon: String?) {
        if (comment.kind == "more") {
            visibility = View.GONE
            return
        }
        binding.userName.text = comment.data?.author
        isSaved = comment.data?.saved == true
        binding.deleteText.isVisible = isSaved
        setUserIcon(userIcon)
        setDate(comment)
        setText(comment)
        binding.loadBtn.setOnClickListener {
            save(binding, comment)
        }
        comment.data?.depth?.let { setMargin((it) * 100) }
    }

    fun setMoreCommentsBtn(comment: Comment) {
        binding.commentText.text = "Show comments"
        binding.commentText.setTextColor(ContextCompat.getColor(context, R.color.purple_600))
        binding.commentText.setPadding(50)
        binding.commentText.textSize = 20F
        comment.data?.depth?.let { setMargin((it) * 100) }
        binding.header.visibility = View.GONE
        binding.footer.visibility = View.GONE
        this@CommentItemViewLight.setOnClickListener {
            onLoadChildrenClick(childrenToLoad!!, this)
            this@CommentItemViewLight.visibility = View.GONE
        }
    }

    fun setIndexForReplyViews(index: Int, children: List<String>) {
        indexForReplyViews = index
        childrenToLoad = children
    }

    private fun setMargin(
        start: Int,
    ) {
        var layoutParams = LinearLayoutCompat.LayoutParams(start, LayoutParams.WRAP_CONTENT)
        binding.lines.layoutParams = layoutParams
        layoutParams = LinearLayoutCompat.LayoutParams(60, LayoutParams.WRAP_CONTENT)
        binding.line.layoutParams = layoutParams
        binding.lines.drawLines()
        binding.line.drawLine()
    }

    private fun setUserIcon(userIcon: String?) {
        GlideApp.with(this)
            .asBitmap()
            .load(userIcon)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.icon)
    }

    private fun setDate(comment: Comment) {
        val time = comment.data?.createdUtc ?: return
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = time.toLong() * 1000
        binding.createdAt.text =
            android.text.format.DateFormat.format("HH:mm, dd MMM yyyy", calendar).toString()
    }

    private fun setText(comment: Comment) {
        val doc = comment.data?.bodyHtml?.let { Jsoup.parse(it) }
        binding.commentText.text = doc?.body()?.text()
    }


    private fun save(binding: CommentItemLightBinding, comment: Comment) {
        when (isSaved) {
            true -> {
                binding.deleteText.isVisible = false
                onUnSaveClick(comment.data?.name!!)
                isSaved = false
            }
            false -> {
                binding.deleteText.isVisible = true
                onSaveClick(comment.data?.name!!, comment.data?.linkId!!)
                isSaved = true
            }
        }
    }

}

class LinesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPaint: Paint? = null
    private var linesNumber: Int = 0


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint = Paint()
        mPaint?.color = Color.BLACK
        mPaint?.strokeWidth = 2F
        var x: Float

        linesNumber = width / 100

        while (linesNumber > 0) {
            x = (100 * linesNumber) - 50.toFloat()
            canvas.drawLine(x, 0F, x, height.toFloat(), mPaint!!)
            linesNumber--
        }
    }

    fun drawLines() {
        invalidate()
    }
}

class SingleLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPaint: Paint? = null

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint = Paint()
        mPaint?.color = Color.BLACK
        mPaint?.strokeWidth = 2F
        val x: Float = 50.toFloat()
        canvas.drawLine(x, 0F, x, height.toFloat(), mPaint!!)
    }

    fun drawLine() {
        invalidate()
    }
}