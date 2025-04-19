import { Link } from "react-router-dom";
import BookModel from "../../models/BookModel";
import { LeaveAReview } from "../Utils/LeaveAReview";

export const CheckoutAndReviewBox: React.FC<{
    book: BookModel | undefined, mobile: boolean,
    currentLoansCount: number, isAuthenticated: any, isCheckedOut: boolean, checkoutBook: any,
    isReviewLeft: boolean, submitReview: any
}> = (props) => {

    function buttonRender() {
        if (props.isAuthenticated) {
            if (!props.isCheckedOut && props.currentLoansCount < 5) {
                return (
                    <button onClick={() => props.checkoutBook()} className="btn btn-success btn-lg">
                        Checkout
                    </button>
                );
            } else if (props.isCheckedOut) {
                return (
                    <p>
                        <b>
                            Book checked out. Enjoy!
                        </b>
                    </p>
                );
            } else if (!props.isCheckedOut && props.currentLoansCount >= 5) {
                return (
                    <p className="text-danger">
                        Too many books checked out.
                    </p>
                );
            }
        }

        return (
            <Link className="btn btn-success btn-lg" to={'/login'}>
                Sign in
            </Link>
        );
    }

    function reviewRender() {

        if (props.isAuthenticated) {
            if (!props.isReviewLeft) {
                return (
                    <LeaveAReview submitReview={props.submitReview} />
                );
            } else {
                return (
                    <p>
                        <b>Thanks for your review!</b>
                    </p>
                );
            }
        }
        return (
            <div>
                <hr />
                <p>Sign in to be able to leave a review.</p>
            </div>
        );
    }

    return (
        <div className={props.mobile ? 'card d-flex mt-5' : 'card col-12 col-md-4 d-flex mb-5'}>
            <div className="card-body">
                <div className="mt-3">
                    <p>
                        <b>{props.currentLoansCount}/5 </b>
                        books checked out
                    </p>
                    <hr />
                    {props.book && props.book.copiesAvailable && props.book.copiesAvailable > 0 ?
                        <h4 className="text-success">Available</h4>
                        :
                        <h4 className="text-danger">
                            Wait List
                        </h4>
                    }
                    <div className="row">
                        <div className="col-6">
                            <p className="mb-1"><strong>{props.book?.copies}</strong> copies</p>
                        </div>
                        <div className="col-6">
                            <p className="mb-1"><strong>{props.book?.copiesAvailable}</strong> available</p>
                        </div>
                    </div>
                </div>
                {buttonRender()}
                <hr />
                <p className="mt-3">
                    This number can change until placing order has been completed.
                </p>
                {reviewRender()}
            </div>
        </div>
    );
}