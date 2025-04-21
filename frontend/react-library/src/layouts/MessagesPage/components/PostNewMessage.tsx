import { useOktaAuth } from "@okta/okta-react";
import { useState } from "react";
import MessageModel from "../../../models/MessageModel";

export const PostNewMessage = () => {
  const { authState } = useOktaAuth();
  const [title, setTitle] = useState("");
  const [question, setQuestion] = useState("");
  const [displayWarn, setDisplayWarn] = useState(false);
  const [displaySuccess, setDisplaySuccess] = useState(false);

  const submitNewQuestion = async () => {
    if (authState?.isAuthenticated && title && question) {
      const url: string = `${process.env.REACT_APP_BASE_URL}/messages/secure/add/message`;
      const messageRequestModel: MessageModel = new MessageModel(
        title,
        question
      );

      const token = authState?.accessToken?.accessToken;
      const requestOptions = {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(messageRequestModel),
      };
      const response: any = await fetch(url, requestOptions);

      if (!response.ok) {
        throw new Error("Something went wrong while sending message!");
      }

      setTitle("");
      setQuestion("");
      setDisplayWarn(false);
      setDisplaySuccess(true);
    } else {
      setDisplayWarn(true);
      setDisplaySuccess(false);
    }
  };

  return (
    <div className="card mt-3">
      <div className="card-header">Ask question to admin</div>
      <div className="card-body">
        <form method="POST">
          {displaySuccess && (
            <div className="alert alert-success" role="alert">
              Question added successfully
            </div>
          )}
          {displayWarn && (
            <div className="alert alert-danger" role="alert">
              All fields must be filled out
            </div>
          )}
          <div className="mb-3">
            <label className="form-label">Title</label>
            <input
              type="text"
              className="form-control"
              id="exampleFormControlInput1"
              placeholder="Title"
              onChange={(e) => setTitle(e.target.value)}
              value={title}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Question</label>
            <textarea
              className="form-control"
              id="exampleFormControlInput1"
              rows={3}
              onChange={(e) => setQuestion(e.target.value)}
              value={question}
            ></textarea>
          </div>
          <div>
            <button
              type="button"
              className="btn btn-primary mt-3"
              onClick={submitNewQuestion}
            >
              Submit
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
