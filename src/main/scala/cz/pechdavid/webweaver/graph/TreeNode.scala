/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.graph

import scala.collection.mutable

/**
 * Result DTO fro graph search
 *
 * Created: 3/8/13 9:20 PM
 */
case class TreeNode(name: String, children: mutable.MutableList[TreeNode] = mutable.MutableList.empty)
