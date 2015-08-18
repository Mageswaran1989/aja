package com.example.myappforScala

import android.app.{Activity,Dialog}
import android.view.{View,ViewGroup,LayoutInflater}

case class TypedResource[A](id: Int)
case class TypedLayout[A](id: Int)

object TR {


  object layout {
    val main = TypedLayout[android.widget.LinearLayout](R.layout.main)
  }
}

trait TypedFindView extends Any {
  protected def findViewById(id: Int): View
  def findView[A](tr: TypedResource[A]): A = findViewById(tr.id).asInstanceOf[A]
}

object TypedResource {
  implicit class TypedView(val v: View) extends AnyVal with TypedFindView {
    def findViewById(id: Int) = v.findViewById(id)
  }
  implicit class TypedActivity(val a: Activity) extends AnyVal with TypedFindView {
    def findViewById(id: Int) = a.findViewById(id)
  }
  implicit class TypedDialog(val d: Dialog) extends AnyVal with TypedFindView {
    def findViewById(id: Int) = d.findViewById(id)
  }
  implicit class TypedLayoutInflater(val l: LayoutInflater) extends AnyVal {
    def inflate[A](tl: TypedLayout[A], c: ViewGroup, b: Boolean) =
      l.inflate(tl.id, c, b).asInstanceOf[A]
    def inflate[A](tl: TypedLayout[A], c: ViewGroup) =
      l.inflate(tl.id, c).asInstanceOf[A]
    def inflate[A](tl: TypedLayout[A]) = l.inflate(tl.id, null).asInstanceOf[A]
  }
}
