package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.battleshiplib.forEachIndex
import java.io.Serializable
import kotlin.random.Random

/*class ShipsSelector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ShipsView(this))
    }
}
class ShipsView(context: Context) : View(context) {
    private var cellWidth: Int
    private var cellHeight: Int

    private val board: Array<IntArray>
    var ships: List<Ship>

    private val nextButtonRect = Rect(0, 0, 0, 0)
    private val nextButtonPaint = Paint().apply {
        color = Color.GREEN
    }
    private val titlePaint = Paint().apply {
        textSize = 48f
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }
    init {
        // Calculate the width and height of each cell based on the size of the view
        val displayMetrics = resources.displayMetrics
        cellWidth = displayMetrics.widthPixels / BattleshipGrid.DEFAULT_COLUMNS
        cellHeight = displayMetrics.heightPixels / BattleshipGrid.DEFAULT_ROWS

        // Initialize the game board with empty cells
        board = Array(BattleshipGrid.DEFAULT_ROWS) { IntArray(BattleshipGrid.DEFAULT_COLUMNS) }

        // Place the ships on the game board
        ships = placeShips(
            BattleshipGrid.DEFAULT_SHIP_SIZES,
            BattleshipGrid.DEFAULT_COLUMNS,
            BattleshipGrid.DEFAULT_ROWS
        )
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val gridPaint = Paint()
        gridPaint.color = Color.BLACK
        gridPaint.strokeWidth = 5f
        val startX = 0F
        val startY = 100F
        val cellSize = Integer.min(width, height) / 10f
        cellWidth = (width / BattleshipGrid.DEFAULT_COLUMNS.toFloat()).toInt()
        cellHeight = (height / BattleshipGrid.DEFAULT_ROWS.toFloat()).toInt()
        // Draw the grid lines
        for (i in 0..10) {
            val x = startX + i * cellSize
            canvas?.drawLine(x, startY.toFloat(), x, startY + cellSize * 10, gridPaint)
            canvas?.drawLine(
                startX,
                startY + i * cellSize,
                startX + cellSize * 10,
                startY + i * cellSize,
                gridPaint
            )

        }
        val centerX = width  / 2
        val centerY =cellSize*10 +250F



        // Calculate the rect for the next button
        val playButtonWidth = 300
        val playButtonHeight = 100
        val playButtonLeft = centerX - playButtonWidth / 2
        val playButtonTop = centerY - playButtonHeight / 2
        val playButtonRight = centerX + playButtonWidth / 2
        val playButtonBottom = centerY + playButtonHeight / 2
        nextButtonRect.set(playButtonLeft, playButtonTop.toInt(), playButtonRight, playButtonBottom.toInt())
        canvas?.drawRect(nextButtonRect, nextButtonPaint)
        canvas?.drawText("Next", centerX.toFloat(),
            ((playButtonTop+playButtonBottom)/2+10).toFloat(), titlePaint)
        val shipPaint = Paint()
        shipPaint.style=Paint.Style.STROKE
        shipPaint.color = Color.RED
        shipPaint.strokeWidth = 5f
        val textPaint=Paint()
        textPaint.setColor(Color.RED);
        textPaint.setTextSize((cellHeight / 2).toFloat());
        var count=0;
        for (ship in ships) {
            var left = startX + ship.left * cellSize
            var top = startY + ship.top * cellSize
            val right = startX + (ship.right + 1) * cellSize
            val bottom = startY + (ship.bottom + 1) * cellSize
            val rect= RectF(left, top, right, bottom)
            canvas?.drawRect(rect, shipPaint)
            var j=0
            while(j< BattleshipGrid.DEFAULT_SHIP_SIZES[count])
            {

                if(right-left<cellSize)
                {
                    top+=cellSize
                }
                else
                {
                    left+=cellSize
                }
                canvas?.drawText(BattleshipGrid.DEFAULT_SHIP_SIZES[count].toString(), left-cellSize+10, top+cellSize-10, textPaint)
                j++
            }

            count++;

        }
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x.toInt()
                val y = event.y.toInt()
                if (nextButtonRect.contains(x, y)) {
                    // Play button clicked, navigate to another activity
                    val intent = Intent(context, BattleshipViewActivity::class.java)
                    val args = Bundle()
                    args.putParcelableArrayList("ARRAYLIST", ships);
                    intent.putExtra("BUNDLE", args)
                    context.startActivity(intent)
                }
                val cellX = x / cellWidth
                val cellY = y / cellHeight
                Log.d("screen","touched $cellX,$cellY")
                /*if (board[cellY][cellX] == GuessResult.HIT()) {
                    // The cell contains a ship
                    board[cellY][cellX] = HIT
                    // Check if the ship has been sunk
                    val ship = getShipAt(cellX, cellY)
                    if (isShipSunk(ship)) {
                        // The ship has been sunk
                        // Update the UI to show that the ship has been sunk
                    } else {
                        // The ship has been hit but not sunk
                        // Update the UI to show that the ship has been hit
                    }
                } else {
                    // The cell does not contain a ship
                    board[cellY][cellX] = MISS
                    // Update the UI to show that the cell has been missed
                }*/
                invalidate()
            }
        }
        return true
    }
    private fun placeShips(shipSizes: IntArray, columns: Int, rows: Int): List<Ship> {
        // Implement the logic to place the ships on the game board
        val ships = mutableListOf<Ship>()
        for (size in shipSizes) {
            var placed = false
            while (!placed) {
                // Generate a random starting position for the ship
                val startX = Random.nextInt(columns)
                val startY = Random.nextInt(rows)

                // Generate a random orientation for the ship
                val isHorizontal = Random.nextBoolean()

                // Create the ship object
                val ship: Ship = object : Ship {
                    override val top: Int = startY
                    override val left: Int = startX
                    override val bottom: Int = if (isHorizontal) startY else startY + size - 1
                    override val right: Int = if (isHorizontal) startX + size - 1 else startX
                }
                if (ship.left >= 0 && ship.right < BattleshipGrid.DEFAULT_COLUMNS && ship.top >= 0 && ship.bottom < BattleshipGrid.DEFAULT_ROWS) {
                    var overlaps = false
                    for (otherShip in ships) {
                        otherShip.forEachIndex { x, y ->
                            if (ship.columnIndices.contains(x) && ship.rowIndices.contains(y)) {
                                overlaps = true
                                return@forEachIndex
                            }
                        }
                    }
                    if (!overlaps) {
                        // The ship does not overlap with any other ships
                        ships.add(ship)
                        placed = true
                    }
                }

                // Check if the ship overlaps with any other ships

            }
        }
        return ships

    }

}*/