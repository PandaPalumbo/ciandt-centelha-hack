package io.realworld.layout.articles

import io.realworld.ConduitManager
import io.realworld.ConduitState
import io.realworld.model.Comment
import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.form.form
import io.kvision.form.text.TextAreaInput
import io.kvision.form.text.textAreaInput
import io.kvision.html.*
import io.kvision.types.toStringF
import io.realworld.model.Article


fun Container.articleComment(state: ConduitState, comment: Comment, article: Article, depth: Int = 0) {
    // Set class based on whether it's a reply
    val commentClass = "comment-body"
    val blockClass = "card-block"
    val indentClass = if (depth > 0) "reply" else "" // Add a class for replies

    div(className = "$commentClass $indentClass") {
        div(className = "card") {
            div(className = blockClass) {
                p(comment.body, className = "card-text")
            }
            div(className = "card-footer", rich = true) {
                val imageSrc = comment.author?.image?.ifBlank { null } ?: "https://static.productionready.io/images/smiley-cyrus.jpg"
                link("", "#/@${comment.author?.username}", className = "comment-author") {
                    image(imageSrc, className = "comment-author-img")
                }
                +"   "
                link(comment.author?.username ?: "", "#/@${comment.author?.username}", className = "comment-author")
                val createdAtFormatted = comment.createdAt?.toStringF("MMMM D, YYYY")
                span(createdAtFormatted, className = "date-posted")

                // Delete option for the comment author
                if (state.user != null && state.user.username == comment.author?.username) {
                    span(className = "mod-options") {
                        tag(TAG.I, className = "ion-trash-a").onClick {
                            ConduitManager.articleCommentDelete(article.slug!!, comment.id!!)
                        }
                    }
                }

                // Reply button
                if (state.user != null && depth == 0) {
                    span(className = "mod-options") {
                        button("Reply", className = "reply-btn").onClick {
                            ConduitManager.articleCommentIsReplying(true, comment.id)
                        }
                    }
                }
            }
        }

        // Show reply form if applicable
        if (state.isReplying && state.replyingToCommentId == comment.id && state.user != null && depth == 0) {
            form(className = "card reply-form reply") {
                lateinit var commentInput: TextAreaInput
                div(className = "card-block") {
                    commentInput = textAreaInput(rows = 3, className = "form-control") {
                        placeholder = "Write a comment..."
                    }
                }
                div(className = "card-footer") {
                    image(state.user.image?.ifBlank { null }, className = "comment-author-img")
                    button("Post Reply",className = "reply-btn", ) {
                        size = ButtonSize.SMALL
                        onClick {
                            ConduitManager.articleCommentIsReplying(false, comment.id)
                            ConduitManager.articleComment(article.slug!!, commentInput.value, comment.id!!)
                        }
                    }
                }
            }
        }
    }
}