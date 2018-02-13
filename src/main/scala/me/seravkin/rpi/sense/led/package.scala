package me.seravkin.rpi.sense

package object led {

  /** RGB 888 Color */
  final case class Color(red: Int, green: Int, blue: Int) {

    // RGB565 Values
    private[this] val r = ((red >> 3) & 0x1F).toShort
    private[this] val g = ((green >> 2) & 0x3F).toShort
    private[this] val b = ((blue >> 3) & 0x1F).toShort

    /** Converts to rgb565
      * @return RGB565 values written in Short
      */
    def toRgb565: Short =
      ((r << 11) + (g << 5) + b).toShort
  }

  implicit class ShortOps(short: Short) {
    /** Reverses byte order in Short */
    def reverseByteOrder: Short = {
      val i = short & 0xffff
      val reversed = (i & 0xff00) >>> 8 | (i & 0x00ff) << 8
      reversed.toShort
    }
  }

  implicit class IntOps(int: Int) {
    /** Converts int value from RGB565 to RGB888 */
    def toRgb888: Color =
      Color(((int >>> 11) & 0x1F) * 255 / 31, ((int >>> 5) & 0x3F) * 255 / 63, (int & 0x1F) * 255 / 31)
  }

  /** Coordinates for SenseHatScreen */
  final case class Point(x: Int, y: Int)

  /** Possible LED errors in inputs */
  sealed trait LedError

  /** Point coordinates are out of bounds (8x8) */
  final case object PointIsOutOfBounds extends LedError
  /** Array size is out of bounds (8x8) */
  final case object ArrayHasInvalidSize extends LedError
  /** Color values (r,g,b) are out of bounds (0-255) */
  final case object ColorHasInvalidValues extends LedError

}
