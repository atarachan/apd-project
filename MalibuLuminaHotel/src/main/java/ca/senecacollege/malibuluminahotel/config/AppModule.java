package ca.senecacollege.malibuluminahotel.config;

import ca.senecacollege.malibuluminahotel.security.AuthenticationService;
import ca.senecacollege.malibuluminahotel.security.IAuthenticationService;
import ca.senecacollege.malibuluminahotel.services.ActivityLogService;
import ca.senecacollege.malibuluminahotel.services.BookingService;
import ca.senecacollege.malibuluminahotel.services.DiscountService;
import ca.senecacollege.malibuluminahotel.services.FeedbackService;
import ca.senecacollege.malibuluminahotel.services.IActivityLogService;
import ca.senecacollege.malibuluminahotel.services.IBookingService;
import ca.senecacollege.malibuluminahotel.services.IDiscountService;
import ca.senecacollege.malibuluminahotel.services.IFeedbackService;
import ca.senecacollege.malibuluminahotel.services.ILoyaltyService;
import ca.senecacollege.malibuluminahotel.services.IPaymentService;
import ca.senecacollege.malibuluminahotel.services.IReportService;
import ca.senecacollege.malibuluminahotel.services.IWaitlistService;
import ca.senecacollege.malibuluminahotel.services.LoyaltyService;
import ca.senecacollege.malibuluminahotel.services.PaymentService;
import ca.senecacollege.malibuluminahotel.services.ReportService;
import ca.senecacollege.malibuluminahotel.services.WaitlistService;
import ca.senecacollege.malibuluminahotel.repositories.ActivityLogRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.AddOnRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.AdminUserRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.BillRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.FeedbackRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.GuestRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IActivityLogRepository;
import ca.senecacollege.malibuluminahotel.repositories.IAddOnRepository;
import ca.senecacollege.malibuluminahotel.repositories.IAdminUserRepository;
import ca.senecacollege.malibuluminahotel.repositories.IBillRepository;
import ca.senecacollege.malibuluminahotel.repositories.IFeedbackRepository;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.repositories.ILoyaltyAccountRepository;
import ca.senecacollege.malibuluminahotel.repositories.ILoyaltyTransactionRepository;
import ca.senecacollege.malibuluminahotel.repositories.IPaymentRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationItemAddOnRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationItemRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomTypeRepository;
import ca.senecacollege.malibuluminahotel.repositories.IWaitlistEntryRepository;
import ca.senecacollege.malibuluminahotel.repositories.LoyaltyAccountRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.LoyaltyTransactionRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.PaymentRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.ReservationItemAddOnRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.ReservationItemRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.ReservationRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.RoomRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.RoomTypeRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.WaitlistEntryRepositoryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IActivityLogService.class).to(ActivityLogService.class);
        bind(IBookingService.class).to(BookingService.class);
        bind(IDiscountService.class).to(DiscountService.class);
        bind(IFeedbackService.class).to(FeedbackService.class);
        bind(ILoyaltyService.class).to(LoyaltyService.class);
        bind(IPaymentService.class).to(PaymentService.class);
        bind(IReportService.class).to(ReportService.class);
        bind(IWaitlistService.class).to(WaitlistService.class).in(Scopes.SINGLETON);
        bind(IAuthenticationService.class).to(AuthenticationService.class).in(Scopes.SINGLETON);

        bind(IActivityLogRepository.class).to(ActivityLogRepositoryImpl.class);
        bind(IAddOnRepository.class).to(AddOnRepositoryImpl.class);
        bind(IAdminUserRepository.class).to(AdminUserRepositoryImpl.class);
        bind(IBillRepository.class).to(BillRepositoryImpl.class);
        bind(IFeedbackRepository.class).to(FeedbackRepositoryImpl.class);
        bind(IGuestRepository.class).to(GuestRepositoryImpl.class);
        bind(ILoyaltyAccountRepository.class).to(LoyaltyAccountRepositoryImpl.class);
        bind(ILoyaltyTransactionRepository.class).to(LoyaltyTransactionRepositoryImpl.class);
        bind(IPaymentRepository.class).to(PaymentRepositoryImpl.class);
        bind(IReservationItemAddOnRepository.class).to(ReservationItemAddOnRepositoryImpl.class);
        bind(IReservationItemRepository.class).to(ReservationItemRepositoryImpl.class);
        bind(IReservationRepository.class).to(ReservationRepositoryImpl.class);
        bind(IRoomRepository.class).to(RoomRepositoryImpl.class);
        bind(IRoomTypeRepository.class).to(RoomTypeRepositoryImpl.class);
        bind(IWaitlistEntryRepository.class).to(WaitlistEntryRepositoryImpl.class);
    }
}
