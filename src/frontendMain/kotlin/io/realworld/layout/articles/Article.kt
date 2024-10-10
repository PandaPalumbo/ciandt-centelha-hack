package io.realworld.layout.articles

import io.realworld.ConduitManager
import io.realworld.ConduitState
import io.realworld.View
import io.realworld.helpers.marked
import io.kvision.core.Container
import io.kvision.form.form
import io.kvision.form.text.TextAreaInput
import io.kvision.form.text.textAreaInput
import io.kvision.html.*
import io.kvision.utils.obj
import io.realworld.model.Article
import io.realworld.model.Comment

// Function to organize comments by parent ID
fun organizeComments(comments: List<Comment>): Map<Int?, List<Comment>> {
    return comments.groupBy { it.parentCommentId }
}

// Recursive function to render comments
fun Container.renderComments(state: ConduitState, comments: List<Comment>, article: Article, parentId: Int? = null, depth: Int = 0) {
    val organizedComments = organizeComments(comments)

    // Render comments that belong to the current parentId
    organizedComments[parentId]?.forEach { comment ->
        articleComment(state, comment, article, depth) // Pass the current depth
        renderComments(state, comments, article, comment.id, depth + 1) // Increment depth for replies
    }
}

fun Container.article(state: ConduitState) {
    if (state.article != null) {
        val article = state.article
        div(className = "article-page") {
            div(className = "banner") {
                div(className = "container") {
                    h1(article.title)
                    articleMeta(article, state)
                }
            }
            div(className = "container page") {
                div(className = "row article-content") {
                    div(className = "col-md-12") {
                        div(marked(article.body!!, obj { sanitize = true }), rich = true)
                        if (article.tagList.isNotEmpty()) {
                            ul(className = "tag-list") {
                                article.tagList.forEach {
                                    li(it, className = "tag-default tag-pill tag-outline")
                                }
                            }
                        }
                    }
                }
                tag(TAG.HR)
                div(className = "article-actions") {
                    articleMeta(article, state)
                }
                div(className = "row") {
                    div(className = "col-xs-12 col-md-8 offset-md-2") {
                        if (state.user != null) {
                            form(className = "card comment-form") {
                                lateinit var commentInput: TextAreaInput
                                div(className = "card-block") {
                                    commentInput = textAreaInput(rows = 3, className = "form-control") {
                                        placeholder = "Write a comment..."
                                    }
                                }
                                div(className = "card-footer") {
                                    image(state.user.image?.ifBlank { null }, className = "comment-author-img")
                                    button("Post Comment") {
                                        size = ButtonSize.SMALL
                                        onClick {
                                            ConduitManager.articleComment(article.slug!!, commentInput.value)
                                        }
                                    }
                                }
                            }
                        } else {
                            p(
                                "<a href=\"#${View.LOGIN.url}\">Sign in</a> or <a href=\"#${View.REGISTER.url}\">sign up</a> to add comments on this article.",
                                rich = true
                            )
                        }
                        state.articleComments?.let { comments ->
                            renderComments(state, comments, article)
                        }
                    }
                }
            }
        }
    }
}
