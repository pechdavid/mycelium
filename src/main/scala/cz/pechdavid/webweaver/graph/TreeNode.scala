package cz.pechdavid.webweaver.graph

import scala.collection.mutable

/**
 * Created: 3/8/13 9:20 PM
 */
case class TreeNode(name: String, children: mutable.MutableList[TreeNode] = mutable.MutableList.empty)
