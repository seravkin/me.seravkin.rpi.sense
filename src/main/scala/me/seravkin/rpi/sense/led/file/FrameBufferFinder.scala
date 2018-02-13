package me.seravkin.rpi.sense.led.file

import better.files._

/** Algebra for finding SenseHat LED's framebuffer
  * @tparam F Side effect
  */
trait FrameBufferFinder[F[_]] {
  /** Get file of SenseHat LED
    * @return File or None if not found
    */
  def get: F[Option[File]]
}
