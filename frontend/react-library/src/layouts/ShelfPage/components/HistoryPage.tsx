import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import HistoryModel from "../../../models/HistoryModel";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import { Link } from "react-router-dom";
import { Pagination } from "../../Utils/Pagination";

export const HistoryPage = () => {
  const { authState } = useOktaAuth();
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
  const [httpError, setHttpError] = useState(null);

  // Histories
  const [histories, setHistories] = useState<HistoryModel[]>([]);

  // Paginations
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchUserHistory = async () => {
      if (!authState?.isAuthenticated) {
        setIsLoadingHistory(false);
        return;
      }

      try {
        const userEmail = authState.accessToken?.claims.sub;
        const pageParam = currentPage - 1;
        const sizeParam = 5;
        const url = `${process.env.REACT_APP_BASE_URL}/histories/search/findBooksByUserEmail?userEmail=${userEmail}&page=${pageParam}&size=${sizeParam}`;
        const requestOptions = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        };
        const response = await fetch(url, requestOptions);

        if (!response.ok) {
          throw new Error("Something went wrong while fetching history.");
        }

        const data = await response.json();

        setHistories(data._embedded?.histories || []);
        setTotalPages(data.page?.totalPages || 0);
      } catch (error: any) {
        setHttpError(error.message);
      } finally {
        setIsLoadingHistory(false);
      }
    };

    fetchUserHistory();
  }, [authState, currentPage]);

  if (isLoadingHistory) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="mt-2">
      {histories.length > 0 ? (
        <>
          <h5>Recent History:</h5>
          {histories.map((history) => (
            <div key={history.id}>
              <div className="card mt-3 shadow p-3 mb-3 bg-body rounded">
                <div className="row g-0">
                  <div className="col-md-2">
                    <div className="d-none d-lg-block">
                      {history.img ? (
                        <img
                          src={history.img}
                          width={123}
                          height={196}
                          alt="Book"
                        />
                      ) : (
                        <img
                          src={require("./../../../Images/BooksImages/book-luv2code-1000.png")}
                          width={123}
                          height={196}
                          alt="Default"
                        />
                      )}
                    </div>
                    <div className="d-lg-none d-flex justify-content-center align-items-center">
                      {history.img ? (
                        <img
                          src={history.img}
                          width={123}
                          height={196}
                          alt="Book"
                        />
                      ) : (
                        <img
                          src={require("./../../../Images/BooksImages/book-luv2code-1000.png")}
                          width={123}
                          height={196}
                          alt="Default"
                        />
                      )}
                    </div>
                  </div>
                  <div className="col">
                    <div className="card-body">
                      <h5 className="card-title">{history.author}</h5>
                      <h4>{history.title}</h4>
                      <p className="card-text">{history.description}</p>
                      <hr />
                      <p className="card-text">
                        Checked out on {history.checkoutDate}
                      </p>
                      <p className="card-text">
                        Returned on {history.returnedDate}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <hr />
            </div>
          ))}
        </>
      ) : (
        <>
          <h3 className="mt-3">Currently no history: </h3>
          <Link className="btn btn-primary" to={"search"}>
            Search for new books.
          </Link>
        </>
      )}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          paginate={paginate}
          totalPages={totalPages}
        />
      )}
    </div>
  );
};
