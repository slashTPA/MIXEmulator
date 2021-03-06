package org.linnando.mixemulator.vm.io.file

import io.scalajs.nodejs.buffer.Buffer
import io.scalajs.nodejs.fs.{FileInputOptions, Fs}
import org.linnando.mixemulator.vm.exceptions.EndOfFileException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object LineAccessFile {
  def readLine(filename: String, position: Long): Future[Array[Char]] =
    Fs.readFileFuture(filename, new FileInputOptions(encoding = "utf8")).map(data => {
      val lines = data.asInstanceOf[String].split("\n")
      if (position < lines.length) lines(position.toInt).toCharArray
      else throw new EndOfFileException
    })

  def appendLine(filename: String, version: Int, chars: Array[Char]): Future[Unit] = {
    val oldFile = s"/$filename/$version"
    val newFile = s"/$filename/${version + 1}"
    for {
      exists <- Fs.existsFuture(oldFile)
      _ <- if (exists) copyFileFuture(oldFile, newFile) else Future {}
      _ <- appendString(newFile, chars.mkString + "\n")
    } yield ()
  }

  // TODO Implement without reading the whole file to memory
  private def copyFileFuture(src: String, dest: String): Future[Unit] =
    for {
      buffer <- Fs.readFileFuture(src).map(_.asInstanceOf[Buffer])
      _ <- Fs.writeFileFuture(dest, buffer)
    } yield ()

  private def appendString(filename: String, str: String): Future[Unit] = {
    Fs.openFuture(filename, "a").flatMap(dest => {
      Fs.writeFuture(dest, str).map(_ => ()) andThen {
        case _ => Fs.closeFuture(dest)
      }
    })
  }

  def appendNewPage(filename: String, version: Int): Future[Unit] = {
    val oldFile = s"/$filename/$version"
    val newFile = s"/$filename/${version + 1}"
    for {
      exists <- Fs.existsFuture(oldFile)
      _ <- if (exists) copyFileFuture(oldFile, newFile) else Future {}
      _ <- appendString(newFile, "\f")
    } yield ()
  }

  def initialiseNonVersioned(filename: String): Future[Unit] =
    for {
      exists <- Fs.existsFuture(filename)
      _ <- if (exists) Future {} else saveNonVersioned(filename, "")
    } yield ()

  def saveNonVersioned(filename: String, data: String): Future[Unit] =
    Fs.writeFileFuture(s"/$filename", data)

  def initialiseVersioned(filename: String): Future[Unit] = {
    for {
      _ <- ensureDeviceDirectoryExists(filename)
      versions <- getVersions(filename)
      _ <- Fs.writeFileFuture(s"/$filename/0", "")
      _ <- unlinkVersions(filename, versions.filter(_ != "0"))
    } yield ()
  }

  def getVersions(filename: String): Future[Iterable[String]] =
    Fs.readdirFuture(s"/$filename").map(_.toArray[String])

  private def ensureDeviceDirectoryExists(filename: String): Future[Unit] =
    for {
      exists <- Fs.existsFuture(s"/$filename")
      isDirectory <- if (exists) Fs.statFuture(s"/$filename").map(_.isDirectory()) else Future(false)
      _ <- if (exists && !isDirectory) Fs.unlinkFuture(s"/$filename") else Future {}
      _ <- if (exists && isDirectory) Future {} else Fs.mkdirFuture(s"/$filename")
    } yield ()

  private def unlinkVersions(filename: String, versions: Iterable[String]): Future[Unit] = {
    val futures = versions.map(version => Fs.unlinkFuture(s"/$filename/$version"))
    Future.fold(futures)(())((_, _) => ())
  }

  def getData(filename: String): Future[String] =
    Fs.readFileFuture(s"/$filename", new FileInputOptions(encoding = "utf8")).map(_.asInstanceOf[String])

  def getData(filename: String, version: Int): Future[String] =
    Fs.readFileFuture(s"/$filename/$version", new FileInputOptions(encoding = "utf8")).map(_.asInstanceOf[String])
}
