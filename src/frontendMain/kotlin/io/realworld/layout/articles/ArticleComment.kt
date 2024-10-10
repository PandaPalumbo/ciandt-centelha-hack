package io.realworld.layout.articles

import io.realworld.ConduitManager
import io.realworld.ConduitState
import io.realworld.model.Article
import io.realworld.model.Comment
import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.html.*
import io.kvision.types.toStringF
import io.kvision.utils.px

fun Container.articleComment(state: ConduitState, comment: Comment, article: Article) {
    div(className = "card") {
        div(className = "card-block") {
            p(comment.body, className = "card-text")
        }
        div(className = "card-footer", rich = true) {
            val imageSrc =
                comment.author?.image?.ifBlank { null } ?: "https://static.productionready.io/images/smiley-cyrus.jpg"
            link("", "#/@${comment.author?.username}", className = "comment-author") {
                image(imageSrc, className = "comment-author-img")
            }
            +" &nbsp; "
            button("", "arrow-up-outline", ButtonStyle.OUTLINEPRIMARY, separator = "&nbsp; ") {
                span("Up vote! ${if (comment.upVote == 0) "" else comment.upVote}", className = "upVoteCounter")
                onClick {
                    ConduitManager.articleCommentUpvote(article.slug!!, comment.id!!)
                }
            }
            val createdAtFormatted = comment.createdAt?.toStringF("MMMM D, YYYY")
            span(createdAtFormatted, className = "date-posted")
            if (state.user != null && state.user.username == comment.author?.username) {
                span(className = "mod-options") {
                    tag(TAG.I, className = "ion-trash-a").onClick {
                        ConduitManager.articleCommentDelete(article.slug!!, comment.id!!)
                    }
                }
            }
        }
    }
}
