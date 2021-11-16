public static final MAX_BOOKS_CHECKOUT = 5;
public static final MAX_DAYS_CHECKOUT = 10;

enum TransactionType{
	RESERVE,
	RETURN,
	CHECKOUT;
}

enum BookType {
	PAPERBACK,
	COVERED;
}

enum BookItemType {
	RESERVED,
	ISSUABLE,
	DAMAGED,
	ARCHIVED;
}

enum AvailabilityStatus {
	AVAILABLE,
	NOT_AVAILABLE;
}

enum SearchBy {
	TITLE,
	AUTHOR,
	SUBJECT,
	PUBLICAITON_DATE;
}

class Address {
	String street;
	String city;
	String state;
	Integer zipCode;
}

class Person {
	String id;
	String name;
	String email;
	String phone;
	Address address;
}

public class LibraryManagement {
	private String name;
	private Address address;
}

class BarCode {
	Integer hashNumber;
	String barCode;
}

class Rack {
	String id;
	String rackCode;
}

class Book {
	String id;
	String name;
	String subject;
	BookType bookType;
	List<String> authors;
	String edition;
}

class BookItem {
	String id;
	Book book;
	Date publicaitonDate;
	BarCode barCode;
	Rack rack;
	BookItemType bookItemType;
	AvailabilityStatus availabilityStatus;
}

class Account {
	Person person;
	String username;
	String password;
	Date dateOfCreation = new Date();
	
	public void resetPassword(String newPassword) {
		this.password = newPassword;
	}
}

class Member extends Account {
	private Integer booksIssued = 0;

	public void incrementBooksIssues() {
		if(this.booksIssued + 1 >= MAX_DAYS_CHECKOUT) {
			throw new BookIssueException("Checkout reached max limit");
		}
		++this.booksIssued;
	}
	public void decrementBooksIssues() {
		if(this.booksIssued - 1 < 0) {
			throw new BookIssueException("No book checked out yet");
		}
		--this.booksIssued;
	}
	public List<Book> serachBookItem(String query, SearchBy searchBy) {
		return catalogService.searchBook(query, searchBy);
	}
	public void transactBook(BookItem bookItem, TransactionType transactionType) {
		bookTransactionService.transactBook(this, bookItem, transactionType);
	}
}

class MemberCard {
	String id;
	Member member;
	Date issueDate;
	Date expiryDate;
	BarCode barCode;
}

class Librarian extends Account {
	public void transactBook(Member member, BookItem bookItem, TransactionType transactionType) {
		bookTransactionService.transactBook(member, bookItem, transactionType);
	}
	public void addMember(Member member) {
		onboardService.onboard(member);
	}
	public void removeMember(Member member) {
		onboardService.remove(member);
	}
}

class Admin extends Account {
	public void addBook(Book book) {
		catalogService.addBook(book);
	}
	public void removeBook(Book book) {
		catalogService.removeBook(book);
	}
	public void addBookItem(BookItem bookItem) {
		catalogService.addBookItem(bookItem);
	}
	public void removeBookItem(BookItem bookItem) {
		catalogService.removeBookItem(bookItem);
	}
	public void addLibrarian(Librarian librarian) {
		onboardService.onboard(librarian);
	}
	public void removeLibrarian(Librarian librarian) {
		onboardService.remove(librarian);
	}
}

class System extends Account {
	public Member getMemberDetails(BookItem bookItem) {
		reservationService.getIssuerDetails(bookItem);
	}
	public List<BookItem> getIssuedBooks(Member member) {
		reservationService.getIssuedBooks(member);
	}
}

class BookTransactionService {
	private void checkoutBook(Member member, BookItem bookItem) {
		// write code to checkout book...
		if(!AvailabilityStatus.AVAILABLE.equals(bookkItem.availabilityStatus)) {
			throw new BookUnavailableException("Book is unavailable");
		}
		if(member.booksIssued >= MAX_BOOKS_CHECKOUT) {
			throw new MaxCheckoutLimitExcepiton("Max checkout limit reached");
		}
		if(Objects.nonNull(DataRepository.bookIssuer.get(bookItem))) {
			throw new BookAlreadyCheckedOutException("Book already checked out");
		}
		bookItem.availabilityStatus = AvailabilityStatus.NOT_AVAILABLE;
		member.incrementBooksIssues();
		DataRepository.bookIssuer.put(bookItem.getId(), member);
		DataRepository.booksIssuedByMember.get(member.getId()).add(bookItem);
	}
	private void reserveBook(Member member, BookItem bookItem) {
		// write code to reserve book...
	}
	private void returnBook(Member member, BookItem bookItem) {
		// write code to return book...
	}
	public void transactBook(Member member, BookItem bookItem, TransactionType transactionType) {
		switch(transactionType) {
			case TransactionType.CHECKOUT : this.checkoutBook(member, bookItem);
			break;
			case TransactionType.RESERVE : this.reserveBook(member, bookItem);
			break;
			case TransactionType.RETURN : this.returnBook(member, bookItem);
			break;
			default : throw new InvalidTransacitonException("Invalid transaciton");
		}
	}
}

class CatalogService {
	private List<Book> searchBookByTitle(String query) {
		return DateRepository.booksByTitle(query);
	}
	private List<Book> searchBookByAuthor(String query) {
		return DateRepository.booksByAuthor(query);
	}
	private List<Book> searchBookBySubject(String query) {
		return DateRepository.booksBySubject(query);
	}
	private List<Book> searchBookByPublicationDate(String query) {
		return DateRepository.booksByPublicationDate(query);
	}
	public void addBook(Book book) {
		// ...
	}
	public void removeBook(Book book) {
		// ...
	}
	public void addBookItem(BookItem bookItem) {
		// ...
	}
	public void removeBookItem(BookItem bookItem) {
		// ...
	}
	public List<Book> searchBook(String query, SearchBy searchBy) {
		List<book> searchList = new ArrayList<>();
		switch (searchBy) {
			case SearchBy.TITLE : searchList = this.searchBookByTitle(query);
			break;
			case SearchBy.AUTHOR : searchList = this.searchBookByAuthor(query);
			break;
			case SearchBy.SUBJECT : searchList = this.searchBookBySubject(query);
			break;
			case SearchBy.PUBLICATION_DATE : searchList = this.searchBookByPublicaitonDate(query);
			break;
			default : throw new InvalidSearchFilter("Invalid search filter");
		}
		return searchList;
	}
}

class OnboardService {
	public void onboard(Person person) {
		if(person instance of Member) {
			// code to onboard member and generate memberId
		} else if(person instance of Librarian) {
			// code to onboard librarian and generate.
		} else {
			throw new InvalidPerson("Invalid person role");
		}
	}
}

class ReservationService {
	public Member getIssuerDetails(BookItem bookItem) {
		return DateRepository.bookIssuer.get(bookItem.getId());
	}
	public List<BookItem> getBooksIssuedByMember(Member member) {
		return DataRepository.booksIssuedByMember.get(member.getId());
	}
}

class FineService {

}

static class DateRepository {
	public List<Book> bookList;
	public List<BookItem> bookItemList;
	public HashMap<String, List<Book> > booksByTitle;
	public HashMap<String, List<Book> > booksByAuthor;
	public HashMap<String, List<Book> > booksBySubject;
	public HashMap<Date, List<Book> > booksByPublicaitonDate;
	public HashMap<String, Member> bookIssuer;
	public HashMap<String, List<BookItem> > booksIssuedByMember;
}