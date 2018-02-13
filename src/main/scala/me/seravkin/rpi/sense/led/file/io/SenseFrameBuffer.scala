package me.seravkin.rpi.sense.led.file.io

/** Algebra for writing to Random Access File
  * @tparam F Side Effect
  */
trait SenseFrameBuffer[F[_]] {
  /** Seek file
    * @param long length to seek
    */
  def seek(long: Long): F[Unit]

  /** Writes Short to file
    * @param short Short to write
    */
  def write(short: Short): F[Unit]

  /** Reads Short from file
    * @return Read Short
    */
  def read(): F[Short]

  /** Writes array to file
    * @param shorts Array of shorts to write
    */
  def write(shorts: Array[Short]): F[Unit]

  /** Reads array with specified byte length from file
    * @param length Bytes to read
    * @return Read array
    */
  def read(length: Int): F[Array[Short]]
}