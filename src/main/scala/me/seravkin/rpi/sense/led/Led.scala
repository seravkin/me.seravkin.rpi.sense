package me.seravkin.rpi.sense.led

/** Algebra for working with SenseHat LED screen
  * @tparam F Effect of operations
  */
trait Led[F[_]] {
  /** Set color for one LED
    * @param point Coordinates of LED
    * @param color Color of LED
    */
  def update(point: Point, color: Color): F[Unit]

  /** Get color for one LED
    * @param point Coordinates of LED
    * @return Color of LED
    */
  def apply(point: Point): F[Color]

  /** Set colors for whole LED
    * @param colors Colors to set
    */
  def apply(colors: Array[Array[Color]]): F[Unit]

  /** Get all colors for LED
    */
  def apply(): F[Array[Array[Color]]]

  /** Set color for one LED
    * @param point Coordinates of LED
    * @param color Color of LED
    */
  def update(point: (Int, Int), color: Color): F[Unit] = {
    val (x, y) = point
    update(Point(x, y), color)
  }

  /** Get color for one LED
    * @param point Coordinates of LED
    * @return Color of LED
    */
  def apply(point: (Int, Int)): F[Color] = {
    val (x, y) = point
    apply(Point(x, y))
  }
}
